package ibank.error;

import ibank.domain.AccountType;
import ibank.domain.Money;

/**
 * Thrown when an account does not hold enough money for a withdrawal, transfer or payment.
 */
public class InsufficientFundsException extends BankingException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates an exception.
   *
   * @param accountType the kind of account that was short of funds
   * @param available the balance that is available
   */
  public InsufficientFundsException(AccountType accountType, Money available) {
    super("error.funds", accountType, available);
  }
}
