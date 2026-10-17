package ibank.host;

import ibank.error.TransactionRejectedException;
import java.util.regex.Pattern;

/**
 * The rules a new PIN must follow.
 */
public final class PinPolicy {

  private static final Pattern FOUR_DIGITS = Pattern.compile("\\d{4}");

  private PinPolicy() {
  }

  /**
   * Checks a new PIN.
   *
   * @param currentPin the PIN in use
   * @param newPin the proposed PIN
   * @param confirmation the proposed PIN entered a second time
   * @throws TransactionRejectedException if the PIN is not four digits, uses one repeated digit,
   *     equals the current PIN or does not match its confirmation
   */
  public static void checkNewPin(String currentPin, String newPin, String confirmation)
      throws TransactionRejectedException {
    if (!FOUR_DIGITS.matcher(newPin).matches()) {
      throw new TransactionRejectedException("error.pin.format");
    }
    if (newPin.chars().distinct().count() == 1) {
      throw new TransactionRejectedException("error.pin.weak");
    }
    if (newPin.equals(currentPin)) {
      throw new TransactionRejectedException("error.pin.same");
    }
    if (!newPin.equals(confirmation)) {
      throw new TransactionRejectedException("error.pin.mismatch");
    }
  }
}
