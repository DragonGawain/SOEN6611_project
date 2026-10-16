package ibank.error;

import ibank.domain.Money;

/**
 * Thrown when a withdrawal would take a client past the daily withdrawal limit (assumption A3).
 */
public class LimitExceededException extends BankingException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates an exception.
   *
   * @param limit the daily limit
   * @param remaining how much the client can still withdraw today
   */
  public LimitExceededException(Money limit, Money remaining) {
    super("error.limit.daily", limit, remaining);
  }
}
