package ibank.domain;

import ibank.error.TransactionRejectedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A retail client of the bank, with their accounts and registered payees.
 */
public final class Client {

  private final String id;
  private final String name;
  private final List<Account> accounts = new ArrayList<>();
  private final List<Payee> payees = new ArrayList<>();

  /**
   * Creates a client with no accounts and no payees.
   *
   * @param id the bank's client identifier
   * @param name the client's name
   */
  public Client(String id, String name) {
    this.id = id;
    this.name = name;
  }

  public String id() {
    return id;
  }

  public String name() {
    return name;
  }

  public void addAccount(Account account) {
    accounts.add(account);
  }

  public void addPayee(Payee payee) {
    payees.add(payee);
  }

  public List<Account> accounts() {
    return Collections.unmodifiableList(accounts);
  }

  public List<Payee> payees() {
    return Collections.unmodifiableList(payees);
  }

  /**
   * Finds one of the client's accounts.
   *
   * @param number the account number
   * @return the account
   * @throws TransactionRejectedException if the client has no such account
   */
  public Account account(String number) throws TransactionRejectedException {
    for (Account account : accounts) {
      if (account.number().equals(number)) {
        return account;
      }
    }
    throw new TransactionRejectedException("error.account.unknown");
  }

  /**
   * Finds one of the client's registered payees.
   *
   * @param payeeId the payee identifier
   * @return the payee
   * @throws TransactionRejectedException if the payee is not registered
   */
  public Payee payee(String payeeId) throws TransactionRejectedException {
    for (Payee payee : payees) {
      if (payee.id().equals(payeeId)) {
        return payee;
      }
    }
    throw new TransactionRejectedException("error.payee.unknown");
  }
}
