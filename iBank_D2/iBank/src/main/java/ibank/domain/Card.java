package ibank.domain;

/**
 * A debit card, with its salted PIN hash and its count of failed PIN attempts.
 *
 * <p>The card never stores the PIN itself.
 */
public final class Card {

  /** Number of wrong PINs after which the card is blocked. */
  public static final int MAX_PIN_ATTEMPTS = 3;

  private final String number;
  private final String clientId;
  private String salt;
  private String pinHash;
  private int failedAttempts;
  private boolean blocked;

  /**
   * Creates an active card.
   *
   * @param number the card number
   * @param clientId the identifier of the card holder
   * @param salt the random salt used to hash the PIN
   * @param pinHash the salted hash of the PIN
   */
  public Card(String number, String clientId, String salt, String pinHash) {
    this.number = number;
    this.clientId = clientId;
    this.salt = salt;
    this.pinHash = pinHash;
  }

  public String number() {
    return number;
  }

  public String clientId() {
    return clientId;
  }

  public String salt() {
    return salt;
  }

  public String pinHash() {
    return pinHash;
  }

  public boolean isBlocked() {
    return blocked;
  }

  /**
   * Records a wrong PIN and blocks the card when no attempts remain.
   *
   * @return the number of attempts remaining
   */
  public int recordFailedAttempt() {
    failedAttempts++;
    if (failedAttempts >= MAX_PIN_ATTEMPTS) {
      blocked = true;
    }
    return Math.max(0, MAX_PIN_ATTEMPTS - failedAttempts);
  }

  /** Clears the failed attempt count after a correct PIN. */
  public void resetFailedAttempts() {
    failedAttempts = 0;
  }

  /**
   * Replaces the PIN.
   *
   * @param newSalt a fresh random salt
   * @param newPinHash the salted hash of the new PIN
   */
  public void replacePin(String newSalt, String newPinHash) {
    this.salt = newSalt;
    this.pinHash = newPinHash;
  }

  /** Blocks the card, for example when it is reported stolen. */
  public void block() {
    blocked = true;
  }
}
