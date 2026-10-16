package ibank.error;

/**
 * Thrown when an amount is missing, malformed, not positive or cannot be handled by the ABM.
 */
public class InvalidAmountException extends BankingException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates an exception.
   *
   * @param messageKey the key of the message that explains how to correct the amount
   * @param arguments values inserted into the message
   */
  public InvalidAmountException(String messageKey, Object... arguments) {
    super(messageKey, arguments);
  }
}
