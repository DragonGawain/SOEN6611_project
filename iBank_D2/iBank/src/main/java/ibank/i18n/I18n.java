package ibank.i18n;

import ibank.domain.Account;
import ibank.domain.Money;
import ibank.error.BankingException;
import java.text.NumberFormat;
import java.util.ResourceBundle;

/**
 * Supplies every text the client sees, in English or French.
 *
 * <p>Texts live in {@code messages_en.properties} and {@code messages_fr.properties}. Adding a
 * language means adding one file and one {@link Language} constant; no screen changes.
 */
public final class I18n {

  private static final String BUNDLE = "ibank.i18n.messages";

  private Language language;
  private ResourceBundle bundle;
  private NumberFormat currency;

  /**
   * Creates the text supplier.
   *
   * @param language the language to start in
   */
  public I18n(Language language) {
    setLanguage(language);
  }

  /**
   * Switches language.
   *
   * @param newLanguage the language chosen by the client
   */
  public void setLanguage(Language newLanguage) {
    this.language = newLanguage;
    this.bundle = ResourceBundle.getBundle(BUNDLE, newLanguage.locale(),
        ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES));
    this.currency = NumberFormat.getCurrencyInstance(newLanguage.locale());
  }

  public Language language() {
    return language;
  }

  /** Returns the base name of the message bundles, for tests that compare them. */
  public static String bundleName() {
    return BUNDLE;
  }

  /**
   * Looks up a text and fills in its placeholders {@code {0}}, {@code {1}} and so on.
   *
   * @param key the message key
   * @param arguments the values for the placeholders; money and localizable values are formatted
   * @return the text, or the key itself if the key is missing
   */
  public String text(String key, Object... arguments) {
    String result = bundle.containsKey(key) ? bundle.getString(key) : key;
    for (int i = 0; i < arguments.length; i++) {
      result = result.replace("{" + i + "}", format(arguments[i]));
    }
    return result;
  }

  /**
   * Explains an exception to the client.
   *
   * @param exception the exception
   * @return the message in the current language
   */
  public String message(BankingException exception) {
    return text(exception.messageKey(), exception.arguments());
  }

  /**
   * Formats an amount, for example {@code $1,234.50} or {@code 1 234,50 $}.
   *
   * @param amount the amount
   * @return the formatted amount
   */
  public String money(Money amount) {
    return currency.format(amount.toBigDecimal());
  }

  /**
   * Describes an account by type, masked number and balance.
   *
   * @param account the account
   * @return for example {@code Chequing ****2345 ($2,500.00)}
   */
  public String describe(Account account) {
    return text(account.type().messageKey()) + " " + account.maskedNumber()
        + " (" + money(account.balance()) + ")";
  }

  private String format(Object argument) {
    if (argument instanceof Money) {
      return money((Money) argument);
    }
    if (argument instanceof Localizable) {
      return text(((Localizable) argument).messageKey());
    }
    return String.valueOf(argument);
  }
}
