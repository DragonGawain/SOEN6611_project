package ibank.i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ibank.domain.AccountType;
import ibank.domain.Money;
import ibank.domain.TransactionType;
import ibank.error.LimitExceededException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import org.junit.jupiter.api.Test;

class I18nTest {

  private static Set<String> keys(Language language) {
    return ResourceBundle.getBundle(I18n.bundleName(), language.locale(),
        ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES))
        .keySet();
  }

  @Test
  void englishAndFrenchHaveExactlyTheSameMessages() {
    assertEquals(keys(Language.ENGLISH), keys(Language.FRENCH));
  }

  @Test
  void everyEnumNameHasTranslation() {
    Set<String> french = keys(Language.FRENCH);
    for (AccountType type : AccountType.values()) {
      assertTrue(french.contains(type.messageKey()), type.name());
    }
    for (TransactionType type : TransactionType.values()) {
      assertTrue(french.contains(type.messageKey()), type.name());
      assertTrue(french.contains(type.resultKey()), type.name());
    }
  }

  @Test
  void theFrenchBundleIsUsedEvenWhenTheComputerIsInAnotherLanguage() {
    Locale previous = Locale.getDefault();
    Locale.setDefault(Locale.GERMANY);
    try {
      assertEquals("Retirer de l'argent", new I18n(Language.FRENCH).text("menu.withdraw"));
      assertEquals("Withdraw cash", new I18n(Language.ENGLISH).text("menu.withdraw"));
    } finally {
      Locale.setDefault(previous);
    }
  }

  @Test
  void moneyFollowsCanadianConventions() {
    assertEquals("$1,234.50", new I18n(Language.ENGLISH).money(Money.ofCents(123450)));
    String french = new I18n(Language.FRENCH).money(Money.ofCents(123450));
    assertTrue(french.replaceAll("\\s", " ").matches("1.234,50.\\$"), french);
  }

  @Test
  void exceptionsBecomeFilledInMessages() {
    I18n i18n = new I18n(Language.ENGLISH);
    String message = i18n.message(
        new LimitExceededException(Money.ofDollars(1000), Money.ofDollars(20)));
    assertEquals("This withdrawal would exceed your daily limit of $1,000.00. "
        + "You can still withdraw $20.00 today.", message);
  }
}
