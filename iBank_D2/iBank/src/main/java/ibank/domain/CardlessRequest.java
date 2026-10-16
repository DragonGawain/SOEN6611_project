package ibank.domain;

import java.time.LocalDateTime;

/**
 * A one time withdrawal code that a client created in the bank's mobile app.
 */
public final class CardlessRequest {

  private final String code;
  private final String clientId;
  private final String accountNumber;
  private final Money amount;
  private final LocalDateTime expiresAt;
  private boolean redeemed;

  /**
   * Creates an unused request.
   *
   * @param code the one time code shown in the app
   * @param clientId the client who created the code
   * @param accountNumber the account to withdraw from
   * @param amount the amount to dispense
   * @param expiresAt the moment after which the code can no longer be used
   */
  public CardlessRequest(String code, String clientId, String accountNumber, Money amount,
      LocalDateTime expiresAt) {
    this.code = code;
    this.clientId = clientId;
    this.accountNumber = accountNumber;
    this.amount = amount;
    this.expiresAt = expiresAt;
  }

  public String code() {
    return code;
  }

  public String clientId() {
    return clientId;
  }

  public String accountNumber() {
    return accountNumber;
  }

  public Money amount() {
    return amount;
  }

  public boolean isRedeemed() {
    return redeemed;
  }

  /**
   * Tells whether the code has expired.
   *
   * @param now the current time
   * @return true when {@code now} is after the expiry time
   */
  public boolean isExpiredAt(LocalDateTime now) {
    return now.isAfter(expiresAt);
  }

  /** Marks the code as used, so it cannot be used again. */
  public void markRedeemed() {
    redeemed = true;
  }
}
