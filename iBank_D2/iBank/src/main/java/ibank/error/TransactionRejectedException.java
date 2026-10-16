package ibank.error;

/**
 * Thrown when the bank refuses a request that breaks one of its rules, such as a transfer to the
 * same account or an expired cardless code.
 */
public class TransactionRejectedException extends BankingException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates an exception.
   *
   * @param messageKey the key of the message that explains the rule
   * @param arguments values inserted into the message
   */
  public TransactionRejectedException(String messageKey, Object... arguments) {
    super(messageKey, arguments);
  }
}
