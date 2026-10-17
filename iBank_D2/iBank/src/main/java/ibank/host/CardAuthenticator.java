package ibank.host;

import ibank.domain.Card;
import ibank.error.AuthenticationException;
import ibank.error.BankingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Verifies cards and PINs, counts wrong PINs and blocks a card after three of them.
 */
public final class CardAuthenticator {

  private final Map<String, Card> cards = new HashMap<>();

  public void register(Card card) {
    cards.put(card.number(), card);
  }

  /**
   * Checks a card and PIN.
   *
   * @param cardNumber the card number
   * @param pin the PIN entered
   * @return the card, when the PIN is correct
   * @throws AuthenticationException if the card is unknown or blocked, or the PIN is wrong
   */
  public Card authenticate(String cardNumber, String pin) throws AuthenticationException {
    Card card = find(cardNumber);
    if (card.isBlocked()) {
      throw AuthenticationException.cardBlocked();
    }
    if (!PinHasher.matches(pin, card.salt(), card.pinHash())) {
      int attemptsLeft = card.recordFailedAttempt();
      throw attemptsLeft > 0
          ? AuthenticationException.wrongPin(attemptsLeft)
          : AuthenticationException.cardBlocked();
    }
    card.resetFailedAttempts();
    return card;
  }

  /**
   * Changes a PIN. A wrong current PIN counts as a failed attempt.
   *
   * @param cardNumber the card number
   * @param currentPin the PIN in use
   * @param newPin the new PIN
   * @param confirmation the new PIN entered a second time
   * @throws BankingException if the current PIN is wrong or the new PIN breaks a {@link PinPolicy}
   *     rule
   */
  public void changePin(String cardNumber, String currentPin, String newPin, String confirmation)
      throws BankingException {
    Card card = authenticate(cardNumber, currentPin);
    PinPolicy.checkNewPin(currentPin, newPin, confirmation);
    String salt = PinHasher.newSalt();
    card.replacePin(salt, PinHasher.hash(newPin, salt));
  }

  private Card find(String cardNumber) throws AuthenticationException {
    Card card = cards.get(cardNumber);
    if (card == null) {
      throw AuthenticationException.unknownCard();
    }
    return card;
  }
}
