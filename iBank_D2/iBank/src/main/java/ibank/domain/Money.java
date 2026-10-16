package ibank.domain;

import java.math.BigDecimal;

/**
 * An immutable amount of Canadian dollars.
 *
 * <p>Amounts are stored as whole cents in a {@code long}, so no rounding error can occur.
 */
public final class Money implements Comparable<Money> {

  /** The amount zero. */
  public static final Money ZERO = new Money(0L);

  private static final long CENTS_PER_DOLLAR = 100L;

  private final long cents;

  private Money(long cents) {
    this.cents = cents;
  }

  /**
   * Creates an amount from a number of cents.
   *
   * @param cents the amount in cents
   * @return the amount
   */
  public static Money ofCents(long cents) {
    return new Money(cents);
  }

  /**
   * Creates an amount from a whole number of dollars.
   *
   * @param dollars the amount in dollars
   * @return the amount
   * @throws ArithmeticException if the amount does not fit in a {@code long} of cents
   */
  public static Money ofDollars(long dollars) {
    return new Money(Math.multiplyExact(dollars, CENTS_PER_DOLLAR));
  }

  /** Returns the amount in cents. */
  public long cents() {
    return cents;
  }

  /** Returns true when the amount has no cents part. */
  public boolean isWholeDollars() {
    return cents % CENTS_PER_DOLLAR == 0;
  }

  /** Returns the whole dollars in the amount, ignoring any cents. */
  public long wholeDollars() {
    return cents / CENTS_PER_DOLLAR;
  }

  /**
   * Adds two amounts.
   *
   * @param other the amount to add
   * @return the sum
   */
  public Money plus(Money other) {
    return new Money(Math.addExact(cents, other.cents));
  }

  /**
   * Subtracts an amount from this one.
   *
   * @param other the amount to subtract
   * @return the difference, which may be negative
   */
  public Money minus(Money other) {
    return new Money(Math.subtractExact(cents, other.cents));
  }

  /** Returns true when the amount is above zero. */
  public boolean isPositive() {
    return cents > 0;
  }

  /**
   * Compares this amount with another.
   *
   * @param other the amount to compare with
   * @return true when this amount is strictly greater
   */
  public boolean isGreaterThan(Money other) {
    return cents > other.cents;
  }

  /** Returns the amount as a decimal number of dollars with two decimal places. */
  public BigDecimal toBigDecimal() {
    return BigDecimal.valueOf(cents, 2);
  }

  @Override
  public int compareTo(Money other) {
    return Long.compare(cents, other.cents);
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof Money && ((Money) other).cents == cents;
  }

  @Override
  public int hashCode() {
    return Long.hashCode(cents);
  }

  @Override
  public String toString() {
    return toBigDecimal().toPlainString() + " CAD";
  }
}
