package ibank.error;

/**
 * Thrown when the cash dispenser cannot supply or deliver the notes requested.
 */
public class DeviceException extends BankingException {

  private static final long serialVersionUID = 1L;

  private DeviceException(String messageKey) {
    super(messageKey);
  }

  /** Creates the exception for a machine that does not hold enough notes right now. */
  public static DeviceException cashUnavailable() {
    return new DeviceException("error.device.noCash");
  }

  /** Creates the exception for a hardware fault while notes were being dispensed. */
  public static DeviceException dispenseFault() {
    return new DeviceException("error.device.fault");
  }
}
