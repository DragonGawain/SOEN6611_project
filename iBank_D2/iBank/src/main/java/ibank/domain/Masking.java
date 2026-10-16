package ibank.domain;

/**
 * Hides card and account numbers so that only the last four digits are ever displayed.
 */
public final class Masking {

  private static final int VISIBLE_DIGITS = 4;

  private Masking() {
  }

  /**
   * Masks every character except the last four.
   *
   * @param number a card or account number
   * @return the masked number, for example {@code ****1234}
   */
  public static String lastFour(String number) {
    int start = Math.max(0, number.length() - VISIBLE_DIGITS);
    return "****" + number.substring(start);
  }
}
