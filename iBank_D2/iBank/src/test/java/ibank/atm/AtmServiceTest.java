package ibank.atm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ibank.MutableClock;
import ibank.device.CashDispenser;
import ibank.domain.CashBundle;
import ibank.domain.Money;
import ibank.domain.TransactionType;
import ibank.error.DeviceException;
import ibank.error.InsufficientFundsException;
import ibank.error.InvalidAmountException;
import ibank.error.LimitExceededException;
import ibank.error.TransactionRejectedException;
import ibank.host.InMemoryBankHost;
import ibank.host.SampleData;
import java.time.LocalDateTime;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AtmServiceTest {

  private CashDispenser dispenser;
  private AtmService atm;

  @BeforeEach
  void startSession() throws Exception {
    InMemoryBankHost host = SampleData.createHost(
        new MutableClock(LocalDateTime.of(2026, 10, 19, 10, 0), SampleData.ZONE));
    dispenser = new CashDispenser(50, 20);
    atm = new AtmService(host, dispenser);
    atm.start(SampleData.AMELIE_CARD, SampleData.AMELIE_PIN);
  }

  private Money chequing() throws Exception {
    return atm.session().client().account(SampleData.AMELIE_CHEQUING).balance();
  }

  @Test
  void withdrawalDebitsTheAccountAndDispensesNotes() throws Exception {
    atm.withdraw(SampleData.AMELIE_CHEQUING, Money.ofDollars(60));
    assertEquals(Money.ofDollars(2440), chequing());
    assertEquals(47, dispenser.twenties());
    assertEquals(Money.ofDollars(940), atm.remainingDailyLimit());
  }

  @Test
  void undispensableAmountIsRefusedBeforeTheAccountIsTouched() throws Exception {
    assertThrows(InvalidAmountException.class,
        () -> atm.withdraw(SampleData.AMELIE_CHEQUING, Money.ofDollars(30)));
    assertEquals(Money.ofDollars(2500), chequing());
  }

  @Test
  void dispenserFaultGivesTheMoneyAndTheDailyLimitBack() throws Exception {
    dispenser.setFaulty(true);
    assertThrows(DeviceException.class,
        () -> atm.withdraw(SampleData.AMELIE_CHEQUING, Money.ofDollars(200)));
    assertEquals(Money.ofDollars(2500), chequing());
    assertEquals(Money.ofDollars(1000), atm.remainingDailyLimit());
  }

  @Test
  void theDailyLimitAppliesAcrossWithdrawals() throws Exception {
    atm.withdraw(SampleData.AMELIE_CHEQUING, Money.ofDollars(900));
    assertThrows(LimitExceededException.class,
        () -> atm.withdraw(SampleData.AMELIE_SAVINGS, Money.ofDollars(120)));
    atm.withdraw(SampleData.AMELIE_SAVINGS, Money.ofDollars(100));
    assertEquals(Money.ZERO, atm.remainingDailyLimit());
  }

  @Test
  void cardlessCodeWorksOnceWithoutCard() throws Exception {
    atm.end();
    assertEquals(TransactionType.CARDLESS_WITHDRAWAL,
        atm.withdrawWithCode(SampleData.CARDLESS_CODE).type());
    assertThrows(TransactionRejectedException.class,
        () -> atm.withdrawWithCode(SampleData.CARDLESS_CODE));
  }

  @Test
  void cashDepositIsCredited() throws Exception {
    atm.depositCash(SampleData.AMELIE_CHEQUING,
        CashBundle.of(Collections.singletonMap(20, 5)));
    assertEquals(Money.ofDollars(2600), chequing());
  }

  @Test
  void lowBalanceClientCannotOverdraw() throws Exception {
    atm.end();
    atm.start(SampleData.JORDAN_CARD, SampleData.JORDAN_PIN);
    InsufficientFundsException e = assertThrows(InsufficientFundsException.class,
        () -> atm.withdraw(SampleData.JORDAN_CHEQUING, Money.ofDollars(60)));
    assertEquals(Money.ofCents(4550), e.arguments()[1]);
  }

  @Test
  void noTransactionIsPossibleWithoutSession() {
    atm.end();
    assertFalse(atm.hasSession());
    assertThrows(IllegalStateException.class,
        () -> atm.withdraw(SampleData.AMELIE_CHEQUING, Money.ofDollars(20)));
  }

  @Test
  void theSessionKeepsTheCardButNeverThePin() {
    assertTrue(atm.hasSession());
    assertEquals("****1234", atm.session().maskedCard());
  }
}
