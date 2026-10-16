package ibank.domain;

import java.time.LocalDateTime;

/**
 * An immutable record of one completed transaction, used for confirmations and receipts.
 */
public final class TransactionRecord {

  private final String reference;
  private final TransactionType type;
  private final Money amount;
  private final String accountNumber;
  private final AccountType accountType;
  private final Money balanceAfter;
  private final String counterparty;
  private final LocalDateTime time;

  /**
   * Records a transaction against an account, capturing the account balance at this moment.
   *
   * @param reference the unique transaction reference
   * @param type the kind of transaction
   * @param amount the amount moved
   * @param account the account debited or credited
   * @param counterparty the other side, such as a payee name; empty when there is none
   * @param time when the transaction happened
   */
  public TransactionRecord(String reference, TransactionType type, Money amount, Account account,
      String counterparty, LocalDateTime time) {
    this.reference = reference;
    this.type = type;
    this.amount = amount;
    this.accountNumber = account.number();
    this.accountType = account.type();
    this.balanceAfter = account.balance();
    this.counterparty = counterparty;
    this.time = time;
  }

  public String reference() {
    return reference;
  }

  public TransactionType type() {
    return type;
  }

  public Money amount() {
    return amount;
  }

  public String accountNumber() {
    return accountNumber;
  }

  public AccountType accountType() {
    return accountType;
  }

  public Money balanceAfter() {
    return balanceAfter;
  }

  public String counterparty() {
    return counterparty;
  }

  public LocalDateTime time() {
    return time;
  }
}
