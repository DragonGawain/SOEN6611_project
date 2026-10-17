package ibank.device;

import ibank.domain.Money;
import ibank.error.DeviceException;
import ibank.error.InvalidAmountException;

/**
 * The cash dispenser, which holds $20 and $50 notes only (assumption A2).
 */
public final class CashDispenser {

  private static final int TWENTY = 20;
  private static final int FIFTY = 50;
  private static final int THIRTY = 30;
  private static final int TEN = 10;

  private int twenties;
  private int fifties;
  private boolean faulty;

  /**
   * Creates a dispenser loaded with notes.
   *
   * @param twenties the number of $20 notes loaded
   * @param fifties the number of $50 notes loaded
   */
  public CashDispenser(int twenties, int fifties) {
    this.twenties = twenties;
    this.fifties = fifties;
  }

  /**
   * Tells whether an amount can be made from $20 and $50 notes, assuming unlimited notes.
   *
   * <p>The possible amounts are $20 and every multiple of $10 from $40 up.
   *
   * @param amount the amount requested
   * @return true when the amount can be dispensed
   */
  public static boolean canEverDispense(Money amount) {
    long dollars = amount.wholeDollars();
    return amount.isWholeDollars() && dollars >= TWENTY && dollars % TEN == 0 && dollars != THIRTY;
  }

  /**
   * Chooses notes for an amount, using as many $50 notes as possible so that fewer notes are
   * needed.
   *
   * @param amount the amount requested
   * @return the notes to dispense
   * @throws InvalidAmountException if no mix of $20 and $50 notes makes the amount
   * @throws DeviceException if the machine does not hold enough notes for the amount
   */
  public NotePlan plan(Money amount) throws InvalidAmountException, DeviceException {
    if (!canEverDispense(amount)) {
      throw new InvalidAmountException("error.amount.notes");
    }
    long dollars = amount.wholeDollars();
    for (long fiftyCount = Math.min(dollars / FIFTY, fifties); fiftyCount >= 0; fiftyCount--) {
      long rest = dollars - fiftyCount * FIFTY;
      if (rest % TWENTY == 0 && rest / TWENTY <= twenties) {
        return new NotePlan((int) (rest / TWENTY), (int) fiftyCount);
      }
    }
    throw DeviceException.cashUnavailable();
  }

  /**
   * Pushes notes out of the machine.
   *
   * @param plan the notes chosen by {@link #plan(Money)}
   * @throws DeviceException if the hardware fails or the notes are no longer there
   */
  public void dispense(NotePlan plan) throws DeviceException {
    if (faulty || plan.twenties() > twenties || plan.fifties() > fifties) {
      throw DeviceException.dispenseFault();
    }
    twenties -= plan.twenties();
    fifties -= plan.fifties();
  }

  /**
   * Simulates a hardware fault, for testing and demonstration.
   *
   * @param faulty true to make the next dispense fail
   */
  public void setFaulty(boolean faulty) {
    this.faulty = faulty;
  }

  public int twenties() {
    return twenties;
  }

  public int fifties() {
    return fifties;
  }
}
