package ibank.error;

import java.util.Arrays;

/**
 * Base class for every expected iBank failure.
 *
 * <p>Each exception carries a message key and arguments instead of fixed text, so that the user
 * interface can explain the problem in the language the client chose.
 */
public class BankingException extends Exception {

  private static final long serialVersionUID = 1L;

  private final String messageKey;
  private final transient Object[] arguments;

  /**
   * Creates an exception.
   *
   * @param messageKey the key of the client facing message in the message bundles
   * @param arguments values inserted into the message
   */
  public BankingException(String messageKey, Object... arguments) {
    super(messageKey + " " + Arrays.toString(arguments));
    this.messageKey = messageKey;
    this.arguments = arguments.clone();
  }

  public String messageKey() {
    return messageKey;
  }

  public Object[] arguments() {
    return arguments.clone();
  }
}
