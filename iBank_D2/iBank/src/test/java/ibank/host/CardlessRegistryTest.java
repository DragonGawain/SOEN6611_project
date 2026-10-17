package ibank.host;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ibank.MutableClock;
import ibank.domain.CardlessRequest;
import ibank.domain.Money;
import ibank.error.TransactionRejectedException;
import java.time.Duration;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CardlessRegistryTest {

  private final MutableClock clock =
      new MutableClock(LocalDateTime.of(2026, 10, 19, 12, 0), SampleData.ZONE);
  private final CardlessRegistry registry = new CardlessRegistry(clock);
  private CardlessRequest request;

  @BeforeEach
  void registerCode() {
    request = new CardlessRequest("482915", "C001", "5012345", Money.ofDollars(100),
        LocalDateTime.of(2026, 10, 19, 12, 30));
    registry.register(request);
  }

  @Test
  void validCodeIsFound() throws Exception {
    assertEquals(request, registry.find("482915"));
  }

  @Test
  void unknownOrUsedCodeIsRejected() {
    assertEquals("error.cardless.invalid", assertThrows(TransactionRejectedException.class,
        () -> registry.find("000000")).messageKey());
    request.markRedeemed();
    assertEquals("error.cardless.invalid", assertThrows(TransactionRejectedException.class,
        () -> registry.find("482915")).messageKey());
  }

  @Test
  void codeWorksUntilItsExpiryMinuteButNotAfter() throws Exception {
    clock.advance(Duration.ofMinutes(30));
    assertEquals(request, registry.find("482915"));
    clock.advance(Duration.ofSeconds(1));
    assertEquals("error.cardless.expired", assertThrows(TransactionRejectedException.class,
        () -> registry.find("482915")).messageKey());
  }
}
