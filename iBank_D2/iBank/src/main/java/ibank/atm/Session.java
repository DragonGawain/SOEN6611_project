package ibank.atm;

import ibank.domain.Client;
import ibank.domain.Masking;

/**
 * The period between a correct PIN and the moment the client takes the card back.
 *
 * <p>A session keeps the card number and the client, never the PIN.
 */
public final class Session {

  private final String cardNumber;
  private final Client client;

  /**
   * Starts a session.
   *
   * @param cardNumber the card in the reader
   * @param client the card holder
   */
  public Session(String cardNumber, Client client) {
    this.cardNumber = cardNumber;
    this.client = client;
  }

  public String cardNumber() {
    return cardNumber;
  }

  public String maskedCard() {
    return Masking.lastFour(cardNumber);
  }

  public Client client() {
    return client;
  }
}
