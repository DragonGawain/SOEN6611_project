package ibank.error;

/**
 * Thrown when the bank's central system cannot be reached, so the ABM must refuse every
 * transaction (assumption A6).
 */
public class HostUnavailableException extends BankingException {

  private static final long serialVersionUID = 1L;

  /** Creates the exception. */
  public HostUnavailableException() {
    super("error.host.offline");
  }
}
