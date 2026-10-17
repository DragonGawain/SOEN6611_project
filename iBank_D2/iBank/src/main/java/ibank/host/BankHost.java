package ibank.host;

import ibank.domain.Account;
import ibank.domain.CardlessRequest;
import ibank.domain.CashBundle;
import ibank.domain.Client;
import ibank.domain.Money;
import ibank.domain.TransactionRecord;
import ibank.error.BankingException;
import java.util.List;

/**
 * The bank's central system, which holds accounts and applies the bank's rules.
 *
 * <p>The ABM talks to the bank only through this interface, so the simulated host can be
 * replaced by a real network connection without changing the ABM or its user interface.
 */
public interface BankHost {

  /**
   * Checks a card and PIN.
   *
   * @param cardNumber the number read from the card
   * @param pin the PIN entered by the client
   * @return the card holder
   * @throws BankingException if the host is offline or the card or PIN is not accepted
   */
  Client authenticate(String cardNumber, String pin) throws BankingException;

  /**
   * Changes the PIN of a card after checking the current one.
   *
   * @param cardNumber the card number
   * @param currentPin the PIN in use
   * @param newPin the new PIN
   * @param confirmation the new PIN entered a second time
   * @throws BankingException if the host is offline or a PIN rule is broken
   */
  void changePin(String cardNumber, String currentPin, String newPin, String confirmation)
      throws BankingException;

  /**
   * Reads a client's accounts and their current balances.
   *
   * @param clientId the client
   * @return the accounts
   * @throws BankingException if the host is offline
   */
  List<Account> accounts(String clientId) throws BankingException;

  /**
   * Tells how much cash a client may still withdraw today.
   *
   * @param clientId the client
   * @return the remaining amount
   * @throws BankingException if the host is offline
   */
  Money remainingDailyLimit(String clientId) throws BankingException;

  /**
   * Debits an account for a cash withdrawal.
   *
   * @param clientId the client
   * @param accountNumber the account to debit
   * @param amount the amount to withdraw
   * @return the transaction record
   * @throws BankingException if the host is offline, funds are short or the daily limit is reached
   */
  TransactionRecord withdraw(String clientId, String accountNumber, Money amount)
      throws BankingException;

  /**
   * Finds a valid cardless withdrawal code.
   *
   * @param code the code entered by the client
   * @return the request created in the mobile app
   * @throws BankingException if the host is offline or the code is unknown, used or expired
   */
  CardlessRequest findCardless(String code) throws BankingException;

  /**
   * Debits the account named in a cardless request and marks the code as used.
   *
   * @param request a request returned by {@link #findCardless(String)}
   * @return the transaction record
   * @throws BankingException if the host is offline, funds are short or the daily limit is reached
   */
  TransactionRecord withdrawCardless(CardlessRequest request) throws BankingException;

  /**
   * Gives back the money of a withdrawal whose cash could not be dispensed.
   *
   * @param clientId the client
   * @param original the withdrawal to reverse
   * @return the reversal record
   * @throws BankingException if the account no longer exists
   */
  TransactionRecord reverseWithdrawal(String clientId, TransactionRecord original)
      throws BankingException;

  /**
   * Credits an account with the notes placed in the deposit slot.
   *
   * @param clientId the client
   * @param accountNumber the account to credit
   * @param cash the counted notes
   * @return the transaction record
   * @throws BankingException if the host is offline or the account is unknown
   */
  TransactionRecord depositCash(String clientId, String accountNumber, CashBundle cash)
      throws BankingException;

  /**
   * Credits an account with a scanned cheque.
   *
   * @param clientId the client
   * @param accountNumber the account to credit
   * @param amount the amount written on the cheque
   * @return the transaction record
   * @throws BankingException if the host is offline or the amount is above the cheque limit
   */
  TransactionRecord depositCheque(String clientId, String accountNumber, Money amount)
      throws BankingException;

  /**
   * Moves money between two accounts of the same client.
   *
   * @param clientId the client
   * @param fromNumber the account to debit
   * @param toNumber the account to credit
   * @param amount the amount to move
   * @return the transaction record
   * @throws BankingException if the host is offline, the accounts are the same or funds are short
   */
  TransactionRecord transfer(String clientId, String fromNumber, String toNumber, Money amount)
      throws BankingException;

  /**
   * Pays a registered payee.
   *
   * @param clientId the client
   * @param fromNumber the account to debit
   * @param payeeId the payee
   * @param amount the amount to pay
   * @return the transaction record
   * @throws BankingException if the host is offline, the payee is unknown or funds are short
   */
  TransactionRecord payBill(String clientId, String fromNumber, String payeeId, Money amount)
      throws BankingException;
}
