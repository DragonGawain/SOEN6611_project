package ibank.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MoneyTest {

  @Test
  void dollarsAreStoredAsCents() {
    assertEquals(12550, Money.ofCents(12550).cents());
    assertEquals(Money.ofCents(4000), Money.ofDollars(40));
  }

  @Test
  void arithmeticIsExact() {
    Money total = Money.ofCents(10).plus(Money.ofCents(20));
    assertEquals(Money.ofCents(30), total);
    assertEquals(Money.ofCents(-5), Money.ofCents(25).minus(Money.ofCents(30)));
  }

  @Test
  void overflowIsDetectedInsteadOfWrappingAround() {
    assertThrows(ArithmeticException.class,
        () -> Money.ofCents(Long.MAX_VALUE).plus(Money.ofCents(1)));
  }

  @Test
  void comparisonsAndWholeDollars() {
    assertTrue(Money.ofDollars(2).isGreaterThan(Money.ofCents(199)));
    assertFalse(Money.ZERO.isPositive());
    assertTrue(Money.ofDollars(60).isWholeDollars());
    assertFalse(Money.ofCents(6050).isWholeDollars());
    assertEquals(60, Money.ofCents(6050).wholeDollars());
  }
}
