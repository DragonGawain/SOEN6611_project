package ibank.domain;

import ibank.error.InvalidAmountException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * The bank notes a client places in the envelope free deposit slot, counted by denomination.
 */
public final class CashBundle {

  /** The note denominations, in dollars, that the deposit slot accepts. */
  public static final List<Integer> DENOMINATIONS =
      Collections.unmodifiableList(Arrays.asList(5, 10, 20, 50, 100));

  /** The most notes the slot accepts in one deposit. */
  public static final int MAX_NOTES = 50;

  private final Map<Integer, Integer> counts;

  private CashBundle(Map<Integer, Integer> counts) {
    this.counts = counts;
  }

  /**
   * Creates a bundle after checking the note counts.
   *
   * @param notes the number of notes for each denomination in dollars
   * @return the bundle
   * @throws InvalidAmountException if a denomination is not accepted, a count is negative, the
   *     bundle is empty or it holds more than {@link #MAX_NOTES} notes
   */
  public static CashBundle of(Map<Integer, Integer> notes) throws InvalidAmountException {
    Map<Integer, Integer> copy = new TreeMap<>(notes);
    int total = 0;
    for (Map.Entry<Integer, Integer> entry : copy.entrySet()) {
      if (!DENOMINATIONS.contains(entry.getKey()) || entry.getValue() < 0) {
        throw new InvalidAmountException("error.deposit.notes");
      }
      total += entry.getValue();
    }
    if (total == 0) {
      throw new InvalidAmountException("error.deposit.empty");
    }
    if (total > MAX_NOTES) {
      throw new InvalidAmountException("error.deposit.tooMany", MAX_NOTES);
    }
    return new CashBundle(copy);
  }

  /** Returns the number of notes in the bundle. */
  public int noteCount() {
    int total = 0;
    for (int count : counts.values()) {
      total += count;
    }
    return total;
  }

  /** Returns the value of the bundle. */
  public Money total() {
    return valueOf(counts);
  }

  /**
   * Adds up note counts without checking them, for example to show a running total.
   *
   * @param notes the number of notes for each denomination in dollars
   * @return the value of the notes
   */
  public static Money valueOf(Map<Integer, Integer> notes) {
    Money total = Money.ZERO;
    for (Map.Entry<Integer, Integer> entry : notes.entrySet()) {
      total = total.plus(Money.ofDollars((long) entry.getKey() * entry.getValue()));
    }
    return total;
  }
}
