package ibank.domain;

/**
 * A company that a client has registered for bill payments in online banking.
 */
public final class Payee {

  private final String id;
  private final String name;

  /**
   * Creates a payee.
   *
   * @param id the bank's identifier for the payee
   * @param name the name shown to the client
   */
  public Payee(String id, String name) {
    this.id = id;
    this.name = name;
  }

  public String id() {
    return id;
  }

  public String name() {
    return name;
  }
}
