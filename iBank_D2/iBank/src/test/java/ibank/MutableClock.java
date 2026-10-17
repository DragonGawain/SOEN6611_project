package ibank;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * A clock that tests can move forward, to check daily limits, expiry and 24 hour windows.
 */
public final class MutableClock extends Clock {

  private final ZoneId zone;
  private Instant now;

  /**
   * Creates a clock.
   *
   * @param start the local start time
   * @param zone the time zone
   */
  public MutableClock(LocalDateTime start, ZoneId zone) {
    this.zone = zone;
    this.now = start.atZone(zone).toInstant();
  }

  public void advance(Duration duration) {
    now = now.plus(duration);
  }

  @Override
  public ZoneId getZone() {
    return zone;
  }

  @Override
  public Clock withZone(ZoneId newZone) {
    return Clock.fixed(now, newZone);
  }

  @Override
  public Instant instant() {
    return now;
  }
}
