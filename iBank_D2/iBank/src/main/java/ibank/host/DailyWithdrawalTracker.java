package ibank.host;

import ibank.domain.Money;
import ibank.error.LimitExceededException;
import java.time.Clock;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Tracks the cash each client withdraws per calendar day against the daily limit (assumption A3).
 */
public final class DailyWithdrawalTracker {

  /** The most cash one client may withdraw in one day. */
  public static final Money DAILY_LIMIT = Money.ofDollars(1000);

  private final Clock clock;
  private final Map<String, Money> withdrawnToday = new HashMap<>();
  private LocalDate day;

  /**
   * Creates a tracker.
   *
   * @param clock the clock that decides when a new day starts
   */
  public DailyWithdrawalTracker(Clock clock) {
    this.clock = clock;
    this.day = LocalDate.now(clock);
  }

  /**
   * Tells how much a client may still withdraw today.
   *
   * @param clientId the client
   * @return the remaining amount, never negative
   */
  public Money remaining(String clientId) {
    startNewDayIfNeeded();
    return DAILY_LIMIT.minus(withdrawnToday.getOrDefault(clientId, Money.ZERO));
  }

  /**
   * Checks a withdrawal against the limit without recording it.
   *
   * @param clientId the client
   * @param amount the amount requested
   * @throws LimitExceededException if the amount is more than the client may still withdraw
   */
  public void check(String clientId, Money amount) throws LimitExceededException {
    Money remaining = remaining(clientId);
    if (amount.isGreaterThan(remaining)) {
      throw new LimitExceededException(DAILY_LIMIT, remaining);
    }
  }

  /**
   * Records a completed withdrawal.
   *
   * @param clientId the client
   * @param amount the amount withdrawn
   */
  public void record(String clientId, Money amount) {
    startNewDayIfNeeded();
    withdrawnToday.merge(clientId, amount, Money::plus);
  }

  /**
   * Removes a withdrawal that was reversed.
   *
   * @param clientId the client
   * @param amount the amount reversed
   */
  public void cancel(String clientId, Money amount) {
    startNewDayIfNeeded();
    withdrawnToday.computeIfPresent(clientId, (id, used) -> used.minus(amount));
  }

  private void startNewDayIfNeeded() {
    LocalDate today = LocalDate.now(clock);
    if (!today.equals(day)) {
      withdrawnToday.clear();
      day = today;
    }
  }
}
