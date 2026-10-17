package ibank.host;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ibank.MutableClock;
import ibank.domain.Money;
import ibank.error.LimitExceededException;
import java.time.Duration;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class DailyWithdrawalTrackerTest {

  private final MutableClock clock =
      new MutableClock(LocalDateTime.of(2026, 10, 19, 22, 0), SampleData.ZONE);
  private final DailyWithdrawalTracker tracker = new DailyWithdrawalTracker(clock);

  @Test
  void exactlyTheLimitIsAllowed() {
    tracker.record("C001", Money.ofDollars(600));
    assertDoesNotThrow(() -> tracker.check("C001", Money.ofDollars(400)));
    tracker.record("C001", Money.ofDollars(400));
    assertEquals(Money.ZERO, tracker.remaining("C001"));
  }

  @Test
  void oneDollarOverTheLimitIsRefusedAndTheMessageGivesWhatIsLeft() {
    tracker.record("C001", Money.ofDollars(980));
    LimitExceededException e = assertThrows(LimitExceededException.class,
        () -> tracker.check("C001", Money.ofDollars(21)));
    assertArrayEquals(new Object[] {Money.ofDollars(1000), Money.ofDollars(20)},
        e.arguments());
  }

  @Test
  void theLimitIsPerClientAndResetsAtMidnight() {
    tracker.record("C001", Money.ofDollars(1000));
    assertEquals(Money.ofDollars(1000), tracker.remaining("C002"));
    clock.advance(Duration.ofHours(2));
    assertEquals(Money.ofDollars(1000), tracker.remaining("C001"));
  }

  @Test
  void reversedWithdrawalNoLongerCounts() {
    tracker.record("C001", Money.ofDollars(300));
    tracker.cancel("C001", Money.ofDollars(300));
    assertEquals(Money.ofDollars(1000), tracker.remaining("C001"));
  }
}
