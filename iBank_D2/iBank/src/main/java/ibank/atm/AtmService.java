package ibank.atm;

import ibank.device.CashDispenser;
import ibank.domain.Account;
import ibank.domain.CashBundle;
import ibank.domain.Client;
import ibank.domain.Money;
import ibank.domain.TransactionRecord;
import ibank.error.BankingException;
import ibank.host.BankHost;
import java.util.List;

/**
 * The single entry point the user interface uses to operate the ABM.
 *
 * <p>It owns the current session and hides the bank host and the devices from the screens.
 */
public final class AtmService {

  private final BankHost host;
  private final CashWithdrawal cash;
  private Session session;

  /**
   * Creates the service.
   *
   * @param host the bank's central system
   * @param dispenser the cash dispenser
   */
  public AtmService(BankHost host, CashDispenser dispenser) {
    this.host = host;
    this.cash = new CashWithdrawal(host, dispenser);
  }

  /**
   * Starts a session after checking the card and PIN.
   *
   * @param cardNumber the card in the reader
   * @param pin the PIN entered
   * @return the new session
   * @throws BankingException if the host is offline or the card or PIN is not accepted
   */
  public Session start(String cardNumber, String pin) throws BankingException {
    Client client = host.authenticate(cardNumber, pin);
    session = new Session(cardNumber, client);
    return session;
  }

  public boolean hasSession() {
    return session != null;
  }

  /** Ends the session and forgets the card. */
  public void end() {
    session = null;
  }

  /**
   * Returns the current session.
   *
   * @return the session
   * @throws IllegalStateException if no card has been accepted
   */
  public Session session() {
    if (session == null) {
      throw new IllegalStateException("No card has been accepted");
    }
    return session;
  }

  public List<Account> accounts() {
    return session().client().accounts();
  }

  /**
   * Reads the current balances from the bank.
   *
   * @return the client's accounts
   * @throws BankingException if the host is offline
   */
  public List<Account> balances() throws BankingException {
    return host.accounts(clientId());
  }

  public Money remainingDailyLimit() throws BankingException {
    return host.remainingDailyLimit(clientId());
  }

  public TransactionRecord withdraw(String accountNumber, Money amount) throws BankingException {
    return cash.withCard(clientId(), accountNumber, amount);
  }

  /**
   * Withdraws cash with a one time code from the mobile app; no card is needed.
   *
   * @param code the code entered
   * @return the transaction record
   * @throws BankingException if the code is not valid or the withdrawal fails
   */
  public TransactionRecord withdrawWithCode(String code) throws BankingException {
    return cash.withCode(code);
  }

  public TransactionRecord depositCash(String accountNumber, CashBundle notes)
      throws BankingException {
    return host.depositCash(clientId(), accountNumber, notes);
  }

  public TransactionRecord depositCheque(String accountNumber, Money amount)
      throws BankingException {
    return host.depositCheque(clientId(), accountNumber, amount);
  }

  public TransactionRecord transfer(String fromNumber, String toNumber, Money amount)
      throws BankingException {
    return host.transfer(clientId(), fromNumber, toNumber, amount);
  }

  public TransactionRecord payBill(String fromNumber, String payeeId, Money amount)
      throws BankingException {
    return host.payBill(clientId(), fromNumber, payeeId, amount);
  }

  public void changePin(String currentPin, String newPin, String confirmation)
      throws BankingException {
    host.changePin(session().cardNumber(), currentPin, newPin, confirmation);
  }

  private String clientId() {
    return session().client().id();
  }
}
