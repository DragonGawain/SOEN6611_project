package ibank.error;

/**
 * Thrown when a card cannot be used: it is unknown, blocked, or the PIN is wrong.
 */
public class AuthenticationException extends BankingException {

  private static final long serialVersionUID = 1L;

  private static final String CARD_BLOCKED = "error.card.blocked";

  private AuthenticationException(String messageKey, Object... arguments) {
    super(messageKey, arguments);
  }

  /**
   * Creates the exception for a wrong PIN.
   *
   * @param attemptsLeft how many attempts remain before the card is blocked
   * @return the exception
   */
  public static AuthenticationException wrongPin(int attemptsLeft) {
    return new AuthenticationException("error.pin.wrong", attemptsLeft);
  }

  /** Creates the exception for a card that has been blocked. */
  public static AuthenticationException cardBlocked() {
    return new AuthenticationException(CARD_BLOCKED);
  }

  /** Creates the exception for a card the bank does not recognize. */
  public static AuthenticationException unknownCard() {
    return new AuthenticationException("error.card.unknown");
  }

  /** Returns true when the card is blocked and must be kept by the machine. */
  public boolean isCardBlocked() {
    return CARD_BLOCKED.equals(messageKey());
  }
}
