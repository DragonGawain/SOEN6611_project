package ibank.atm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ibank.domain.Money;
import ibank.error.InvalidAmountException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AmountParserTest {

  @ParameterizedTest(name = "\"{0}\" is {1} cents")
  @CsvSource(delimiter = '|', value = {"40|4000", "125.50|12550", "125,5|12550", "$ 20|2000",
      "0.01|1", "9999999.99|999999999"})
  void validAmountsInEnglishOrFrenchFormat(String text, long cents) throws Exception {
    assertEquals(Money.ofCents(cents), AmountParser.parse(text));
  }

  @ParameterizedTest(name = "\"{0}\" is rejected with {1}")
  @CsvSource(delimiter = '|', value = {"''|error.amount.empty", "'   '|error.amount.empty",
      "abc|error.amount.format", "12.345|error.amount.format", "-5|error.amount.format",
      "10000000|error.amount.format", "0|error.amount.positive", "0.00|error.amount.positive"})
  void invalidAmountsGetSpecificMessage(String text, String key) {
    InvalidAmountException e = assertThrows(InvalidAmountException.class,
        () -> AmountParser.parse(text));
    assertEquals(key, e.messageKey());
  }
}
