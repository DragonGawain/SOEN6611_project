package ibank.atm;

import ibank.domain.Money;
import ibank.error.InvalidAmountException;
import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Turns the text a client types into an amount, accepting both {@code 12.50} and {@code 12,50}.
 */
public final class AmountParser {

  private static final Pattern AMOUNT = Pattern.compile("\\d{1,7}([.,]\\d{1,2})?");

  private AmountParser() {
  }

  /**
   * Parses an amount in dollars.
   *
   * @param text the text typed, which may include a dollar sign and spaces
   * @return the amount
   * @throws InvalidAmountException if the text is empty, malformed or not above zero
   */
  public static Money parse(String text) throws InvalidAmountException {
    String cleaned = text == null ? "" : text.replaceAll("[$\\s\\u00A0]", "");
    if (cleaned.isEmpty()) {
      throw new InvalidAmountException("error.amount.empty");
    }
    if (!AMOUNT.matcher(cleaned).matches()) {
      throw new InvalidAmountException("error.amount.format");
    }
    BigDecimal dollars = new BigDecimal(cleaned.replace(',', '.'));
    Money amount = Money.ofCents(dollars.movePointRight(2).longValueExact());
    if (!amount.isPositive()) {
      throw new InvalidAmountException("error.amount.positive");
    }
    return amount;
  }
}
