package ibank.domain;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ibank.error.InsufficientFundsException;
import ibank.error.InvalidAmountException;
import org.junit.jupiter.api.Test;

class AccountTest {

  private final Account account =
      new Account("5012345", AccountType.CHEQUING, Money.ofDollars(100));

  @Test
  void debitAndCreditChangeTheBalance() throws Exception {
    account.debit(Money.ofDollars(40));
    account.credit(Money.ofCents(1050));
    assertEquals(Money.ofCents(7050), account.balance());
  }

  @Test
  void theWholeBalanceCanBeWithdrawnButNotOneCentMore() throws Exception {
    InsufficientFundsException e = assertThrows(InsufficientFundsException.class,
        () -> account.debit(Money.ofCents(10001)));
    assertArrayEquals(new Object[] {AccountType.CHEQUING, Money.ofDollars(100)}, e.arguments());
    account.debit(Money.ofDollars(100));
    assertEquals(Money.ZERO, account.balance());
  }

  @Test
  void zeroAndNegativeAmountsAreRejected() {
    assertThrows(InvalidAmountException.class, () -> account.credit(Money.ZERO));
    assertThrows(InvalidAmountException.class, () -> account.debit(Money.ofCents(-1)));
  }

  @Test
  void onlyTheLastFourDigitsAreShown() {
    assertEquals("****2345", account.maskedNumber());
    assertEquals("****12", Masking.lastFour("12"));
  }
}
