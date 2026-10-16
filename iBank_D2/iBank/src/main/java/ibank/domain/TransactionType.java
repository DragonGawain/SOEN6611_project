package ibank.domain;

import ibank.i18n.Localizable;

/**
 * The kinds of transaction recorded by the ABM.
 */
public enum TransactionType implements Localizable {
  WITHDRAWAL("txn.withdrawal", "result.withdrawal"),
  CARDLESS_WITHDRAWAL("txn.cardless", "result.withdrawal"),
  CASH_DEPOSIT("txn.cashDeposit", "result.deposit"),
  CHEQUE_DEPOSIT("txn.chequeDeposit", "result.chequeDeposit"),
  TRANSFER("txn.transfer", "result.transfer"),
  BILL_PAYMENT("txn.billPayment", "result.billPayment"),
  REVERSAL("txn.reversal", "result.reversal");

  private final String messageKey;
  private final String resultKey;

  TransactionType(String messageKey, String resultKey) {
    this.messageKey = messageKey;
    this.resultKey = resultKey;
  }

  @Override
  public String messageKey() {
    return messageKey;
  }

  /** Returns the key of the confirmation message shown after a successful transaction. */
  public String resultKey() {
    return resultKey;
  }
}
