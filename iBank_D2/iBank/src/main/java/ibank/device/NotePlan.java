package ibank.device;

import ibank.domain.Money;

/**
 * The notes chosen to make up a withdrawal.
 */
public final class NotePlan {

  private final int twenties;
  private final int fifties;

  /**
   * Creates a plan.
   *
   * @param twenties the number of $20 notes
   * @param fifties the number of $50 notes
   */
  public NotePlan(int twenties, int fifties) {
    this.twenties = twenties;
    this.fifties = fifties;
  }

  public int twenties() {
    return twenties;
  }

  public int fifties() {
    return fifties;
  }

  /** Returns the value of the notes. */
  public Money total() {
    return Money.ofDollars(20L * twenties + 50L * fifties);
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof NotePlan)) {
      return false;
    }
    NotePlan plan = (NotePlan) other;
    return plan.twenties == twenties && plan.fifties == fifties;
  }

  @Override
  public int hashCode() {
    return 31 * twenties + fifties;
  }

  @Override
  public String toString() {
    return fifties + " x $50 + " + twenties + " x $20";
  }
}
