package ibank.host;

import ibank.domain.Account;
import ibank.domain.Card;
import ibank.domain.CardlessRequest;
import ibank.domain.CashBundle;
import ibank.domain.Client;
import ibank.domain.Money;
import ibank.domain.Payee;
import ibank.domain.TransactionRecord;
import ibank.domain.TransactionType;
import ibank.error.BankingException;
import ibank.error.HostUnavailableException;
import ibank.error.InvalidAmountException;
import ibank.error.TransactionRejectedException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

/**
 * A simulated bank host that keeps its data in memory.
 *
 * <p>It can be switched offline to show how the ABM refuses transactions (assumption A6).
 */
public final class InMemoryBankHost implements BankHost {

  /** The largest cheque the ABM accepts; larger cheques must be deposited with a teller. */
  public static final Money MAX_CHEQUE = Money.ofDollars(20000);

  private final BankRecords records;
  private final DailyWithdrawalTracker limits;
  private final LargeCashMonitor cashMonitor;
  private final Clock clock;
  private boolean online = true;
  private long nextReference = 1;

  /**
   * Creates a host.
   *
   * @param records the bank's data
   * @param limits the daily withdrawal tracker
   * @param cashMonitor the large cash transaction monitor
   * @param clock the clock used to time transactions
   */
  public InMemoryBankHost(BankRecords records, DailyWithdrawalTracker limits,
      LargeCashMonitor cashMonitor, Clock clock) {
    this.records = records;
    this.limits = limits;
    this.cashMonitor = cashMonitor;
    this.clock = clock;
  }

  /**
   * Connects or disconnects the simulated host.
   *
   * @param online false to make every request fail as if the network were down
   */
  public void setOnline(boolean online) {
    this.online = online;
  }

  @Override
  public Client authenticate(String cardNumber, String pin) throws BankingException {
    ensureOnline();
    Card card = records.authenticator().authenticate(cardNumber, pin);
    return records.client(card.clientId());
  }

  @Override
  public void changePin(String cardNumber, String currentPin, String newPin, String confirmation)
      throws BankingException {
    ensureOnline();
    records.authenticator().changePin(cardNumber, currentPin, newPin, confirmation);
  }

  @Override
  public List<Account> accounts(String clientId) throws BankingException {
    ensureOnline();
    return records.client(clientId).accounts();
  }

  @Override
  public Money remainingDailyLimit(String clientId) throws BankingException {
    ensureOnline();
    return limits.remaining(clientId);
  }

  @Override
  public TransactionRecord withdraw(String clientId, String accountNumber, Money amount)
      throws BankingException {
    return debitCash(clientId, accountNumber, amount, TransactionType.WITHDRAWAL);
  }

  @Override
  public CardlessRequest findCardless(String code) throws BankingException {
    ensureOnline();
    return records.cardless().find(code);
  }

  @Override
  public TransactionRecord withdrawCardless(CardlessRequest request) throws BankingException {
    TransactionRecord record = debitCash(request.clientId(), request.accountNumber(),
        request.amount(), TransactionType.CARDLESS_WITHDRAWAL);
    request.markRedeemed();
    return record;
  }

  @Override
  public TransactionRecord reverseWithdrawal(String clientId, TransactionRecord original)
      throws BankingException {
    Account account = records.client(clientId).account(original.accountNumber());
    account.credit(original.amount());
    limits.cancel(clientId, original.amount());
    return record(TransactionType.REVERSAL, original.amount(), account, original.reference());
  }

  @Override
  public TransactionRecord depositCash(String clientId, String accountNumber, CashBundle cash)
      throws BankingException {
    ensureOnline();
    Account account = records.client(clientId).account(accountNumber);
    account.credit(cash.total());
    cashMonitor.record(clientId, cash.total());
    return record(TransactionType.CASH_DEPOSIT, cash.total(), account, "");
  }

  @Override
  public TransactionRecord depositCheque(String clientId, String accountNumber, Money amount)
      throws BankingException {
    ensureOnline();
    if (amount.isGreaterThan(MAX_CHEQUE)) {
      throw new InvalidAmountException("error.cheque.max", MAX_CHEQUE);
    }
    Account account = records.client(clientId).account(accountNumber);
    account.credit(amount);
    return record(TransactionType.CHEQUE_DEPOSIT, amount, account, "");
  }

  @Override
  public TransactionRecord transfer(String clientId, String fromNumber, String toNumber,
      Money amount) throws BankingException {
    ensureOnline();
    if (fromNumber.equals(toNumber)) {
      throw new TransactionRejectedException("error.transfer.same");
    }
    Client client = records.client(clientId);
    Account from = client.account(fromNumber);
    Account to = client.account(toNumber);
    from.debit(amount);
    to.credit(amount);
    return record(TransactionType.TRANSFER, amount, from, to.maskedNumber());
  }

  @Override
  public TransactionRecord payBill(String clientId, String fromNumber, String payeeId,
      Money amount) throws BankingException {
    ensureOnline();
    Client client = records.client(clientId);
    Payee payee = client.payee(payeeId);
    Account from = client.account(fromNumber);
    from.debit(amount);
    return record(TransactionType.BILL_PAYMENT, amount, from, payee.name());
  }

  private TransactionRecord debitCash(String clientId, String accountNumber, Money amount,
      TransactionType type) throws BankingException {
    ensureOnline();
    Account account = records.client(clientId).account(accountNumber);
    limits.check(clientId, amount);
    account.debit(amount);
    limits.record(clientId, amount);
    return record(type, amount, account, "");
  }

  private void ensureOnline() throws HostUnavailableException {
    if (!online) {
      throw new HostUnavailableException();
    }
  }

  private TransactionRecord record(TransactionType type, Money amount, Account account,
      String counterparty) {
    String reference = String.format(Locale.ROOT, "IB%06d", nextReference++);
    return new TransactionRecord(reference, type, amount, account, counterparty,
        LocalDateTime.now(clock));
  }
}
