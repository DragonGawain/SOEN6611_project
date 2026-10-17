package ibank.device;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ibank.domain.Money;
import ibank.error.DeviceException;
import ibank.error.InvalidAmountException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CashDispenserTest {

  @ParameterizedTest(name = "{0} cents dispensable: {1}")
  @CsvSource({"1000, false", "2000, true", "3000, false", "4000, true", "5000, true",
      "6000, true", "7000, true", "2500, false", "2050, false", "100000, true"})
  void onlyAmountsMadeOfTwentiesAndFiftiesAreDispensable(long cents, boolean expected) {
    assertEquals(expected, CashDispenser.canEverDispense(Money.ofCents(cents)));
  }

  @ParameterizedTest(name = "${0} is {1} x $20 + {2} x $50")
  @CsvSource({"20, 1, 0", "40, 2, 0", "50, 0, 1", "60, 3, 0", "70, 1, 1", "80, 4, 0",
      "110, 3, 1", "1000, 0, 20"})
  void plansUseAsManyFiftiesAsPossible(int dollars, int twenties, int fifties) throws Exception {
    CashDispenser dispenser = new CashDispenser(100, 100);
    assertEquals(new NotePlan(twenties, fifties), dispenser.plan(Money.ofDollars(dollars)));
  }

  @Test
  void impossibleAmountIsAnInputErrorNotMachineError() {
    CashDispenser dispenser = new CashDispenser(100, 100);
    InvalidAmountException e = assertThrows(InvalidAmountException.class,
        () -> dispenser.plan(Money.ofDollars(30)));
    assertEquals("error.amount.notes", e.messageKey());
  }

  @Test
  void runningOutOfFiftiesFallsBackToTwenties() throws Exception {
    CashDispenser dispenser = new CashDispenser(10, 0);
    assertEquals(new NotePlan(5, 0), dispenser.plan(Money.ofDollars(100)));
    assertThrows(DeviceException.class, () -> dispenser.plan(Money.ofDollars(220)));
  }

  @Test
  void dispensingRemovesNotesAndFaultIsReported() throws Exception {
    CashDispenser dispenser = new CashDispenser(10, 10);
    dispenser.dispense(dispenser.plan(Money.ofDollars(120)));
    assertEquals(9, dispenser.twenties());
    assertEquals(8, dispenser.fifties());
    dispenser.setFaulty(true);
    assertThrows(DeviceException.class, () -> dispenser.dispense(new NotePlan(1, 0)));
    assertEquals(9, dispenser.twenties());
  }
}
