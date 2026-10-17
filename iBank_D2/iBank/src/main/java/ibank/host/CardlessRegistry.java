package ibank.host;

import ibank.domain.CardlessRequest;
import ibank.error.TransactionRejectedException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds the one time withdrawal codes that clients create in the bank's mobile app.
 */
public final class CardlessRegistry {

  private final Clock clock;
  private final Map<String, CardlessRequest> requests = new HashMap<>();

  /**
   * Creates an empty registry.
   *
   * @param clock the clock used to check expiry
   */
  public CardlessRegistry(Clock clock) {
    this.clock = clock;
  }

  public void register(CardlessRequest request) {
    requests.put(request.code(), request);
  }

  /**
   * Finds a request that can still be used.
   *
   * @param code the code entered at the ABM
   * @return the request
   * @throws TransactionRejectedException if the code is unknown, already used or expired
   */
  public CardlessRequest find(String code) throws TransactionRejectedException {
    CardlessRequest request = requests.get(code);
    if (request == null || request.isRedeemed()) {
      throw new TransactionRejectedException("error.cardless.invalid");
    }
    if (request.isExpiredAt(LocalDateTime.now(clock))) {
      throw new TransactionRejectedException("error.cardless.expired");
    }
    return request;
  }
}
