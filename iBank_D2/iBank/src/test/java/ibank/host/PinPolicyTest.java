package ibank.host;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ibank.error.TransactionRejectedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PinPolicyTest {

  @ParameterizedTest(name = "new PIN {1} confirmed as {2} gives {3}")
  @CsvSource({"2468, 123, 123, error.pin.format", "2468, 12345, 12345, error.pin.format",
      "2468, 12a4, 12a4, error.pin.format", "2468, 7777, 7777, error.pin.weak",
      "2468, 2468, 2468, error.pin.same", "2468, 1357, 1375, error.pin.mismatch"})
  void badPinsAreRejected(String current, String fresh, String confirmation, String key) {
    TransactionRejectedException e = assertThrows(TransactionRejectedException.class,
        () -> PinPolicy.checkNewPin(current, fresh, confirmation));
    assertEquals(key, e.messageKey());
  }

  @Test
  void goodPinIsAccepted() {
    assertDoesNotThrow(() -> PinPolicy.checkNewPin("2468", "8023", "8023"));
  }
}
