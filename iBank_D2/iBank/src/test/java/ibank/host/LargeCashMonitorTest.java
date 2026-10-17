package ibank.host;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ibank.MutableClock;
import ibank.domain.Money;
import java.time.Duration;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class LargeCashMonitorTest {

  private final MutableClock clock =
      new MutableClock(LocalDateTime.of(2026, 10, 19, 9, 0), SampleData.ZONE);
  private final LargeCashMonitor monitor = new LargeCashMonitor(clock);

  @Test
  void oneCentBelowTheThresholdIsNotReported() {
    assertFalse(monitor.record("C001", Money.ofCents(999999)));
    assertTrue(monitor.reports().isEmpty());
  }

  @Test
  void exactlyTenThousandDollarsIsReported() {
    assertTrue(monitor.record("C001", Money.ofDollars(10000)));
    assertEquals(Money.ofDollars(10000), monitor.reports().get(0).amount());
  }

  @Test
  void depositsWithinTwentyFourHoursAreAddedUp() {
    assertFalse(monitor.record("C001", Money.ofDollars(6000)));
    clock.advance(Duration.ofHours(23));
    assertTrue(monitor.record("C001", Money.ofDollars(4000)));
  }

  @Test
  void depositsMoreThanTwentyFourHoursApartAreNot() {
    monitor.record("C001", Money.ofDollars(6000));
    clock.advance(Duration.ofHours(25));
    assertFalse(monitor.record("C001", Money.ofDollars(4000)));
  }
}
