package ibank.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ibank.error.InvalidAmountException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class CashBundleTest {

  private static Map<Integer, Integer> notes(int... denominationThenCount) {
    Map<Integer, Integer> notes = new HashMap<>();
    for (int i = 0; i < denominationThenCount.length; i += 2) {
      notes.put(denominationThenCount[i], denominationThenCount[i + 1]);
    }
    return notes;
  }

  @Test
  void totalAddsEveryDenomination() throws Exception {
    CashBundle bundle = CashBundle.of(notes(5, 1, 10, 2, 20, 3, 50, 1, 100, 2));
    assertEquals(Money.ofDollars(335), bundle.total());
    assertEquals(9, bundle.noteCount());
  }

  @Test
  void fiftyNotesAreAcceptedButFiftyOneAreNot() throws Exception {
    assertEquals(50, CashBundle.of(notes(20, 50)).noteCount());
    InvalidAmountException e = assertThrows(InvalidAmountException.class,
        () -> CashBundle.of(notes(20, 51)));
    assertEquals("error.deposit.tooMany", e.messageKey());
  }

  @Test
  void invalidBundlesAreRejectedWithSpecificReason() {
    assertEquals("error.deposit.empty", assertThrows(InvalidAmountException.class,
        () -> CashBundle.of(notes(20, 0))).messageKey());
    assertEquals("error.deposit.notes", assertThrows(InvalidAmountException.class,
        () -> CashBundle.of(notes(2, 1))).messageKey());
    assertEquals("error.deposit.notes", assertThrows(InvalidAmountException.class,
        () -> CashBundle.of(notes(20, -1))).messageKey());
  }
}
