package ibank.atm;

import ibank.device.CashDispenser;
import ibank.device.NotePlan;
import ibank.domain.CardlessRequest;
import ibank.domain.Money;
import ibank.domain.TransactionRecord;
import ibank.error.BankingException;
import ibank.error.DeviceException;
import ibank.host.BankHost;

/**
 * Carries out a withdrawal in the safe order: plan the notes, debit the account, dispense, and
 * give the money back if the notes could not be dispensed.
 */
final class CashWithdrawal {

  private final BankHost host;
  private final CashDispenser dispenser;

  CashWithdrawal(BankHost host, CashDispenser dispenser) {
    this.host = host;
    this.dispenser = dispenser;
  }

  TransactionRecord withCard(String clientId, String accountNumber, Money amount)
      throws BankingException {
    NotePlan plan = dispenser.plan(amount);
    TransactionRecord record = host.withdraw(clientId, accountNumber, amount);
    return dispenseOrReverse(clientId, record, plan);
  }

  TransactionRecord withCode(String code) throws BankingException {
    CardlessRequest request = host.findCardless(code);
    NotePlan plan = dispenser.plan(request.amount());
    TransactionRecord record = host.withdrawCardless(request);
    return dispenseOrReverse(request.clientId(), record, plan);
  }

  private TransactionRecord dispenseOrReverse(String clientId, TransactionRecord record,
      NotePlan plan) throws BankingException {
    try {
      dispenser.dispense(plan);
      return record;
    } catch (DeviceException e) {
      host.reverseWithdrawal(clientId, record);
      throw e;
    }
  }
}
