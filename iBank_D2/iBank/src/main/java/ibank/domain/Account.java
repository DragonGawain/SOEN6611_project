package ibank.domain;

import ibank.error.InsufficientFundsException;
import ibank.error.InvalidAmountException;

/**
 * A client's bank account, held in Canadian dollars.
 */
public final class Account {

  private final String number;
  private final AccountType type;
  private Money balance;

  /**
   * Creates an account.
   *
   * @param number the account number
   * @param type the kind of account
   * @param openingBalance the balance when the account is loaded
   */
  public Account(String number, AccountType type, Money openingBalance) {
    this.number = number;
    this.type = type;
    this.balance = openingBalance;
  }

  public String number() {
    return number;
  }

  public AccountType type() {
    return type;
  }

  public Money balance() {
    return balance;
  }

  /** Returns the account number with all but the last four digits hidden. */
  public String maskedNumber() {
    return Masking.lastFour(number);
  }

  /**
   * Adds money to the account.
   *
   * @param amount a positive amount
   * @throws InvalidAmountException if the amount is zero or negative
   */
  public void credit(Money amount) throws InvalidAmountException {
    requirePositive(amount);
    balance = balance.plus(amount);
  }

  /**
   * Takes money out of the account.
   *
   * @param amount a positive amount no greater than the balance
   * @throws InvalidAmountException if the amount is zero or negative
   * @throws InsufficientFundsException if the amount is greater than the balance
   */
  public void debit(Money amount) throws InvalidAmountException, InsufficientFundsException {
    requirePositive(amount);
    if (amount.isGreaterThan(balance)) {
      throw new InsufficientFundsException(type, balance);
    }
    balance = balance.minus(amount);
  }

  private static void requirePositive(Money amount) throws InvalidAmountException {
    if (!amount.isPositive()) {
      throw new InvalidAmountException("error.amount.positive");
    }
  }
}
