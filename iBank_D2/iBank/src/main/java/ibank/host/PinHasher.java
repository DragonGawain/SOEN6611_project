package ibank.host;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Hashes PINs with a random salt, so that no PIN is ever stored in plain text.
 *
 * <p>This is a simulation. A production ABM would encrypt the PIN in a hardware security module
 * instead.
 */
public final class PinHasher {

  private static final SecureRandom RANDOM = new SecureRandom();
  private static final int SALT_BYTES = 16;

  private PinHasher() {
  }

  /** Creates a new random salt, encoded in Base64. */
  public static String newSalt() {
    byte[] salt = new byte[SALT_BYTES];
    RANDOM.nextBytes(salt);
    return Base64.getEncoder().encodeToString(salt);
  }

  /**
   * Hashes a PIN with SHA 256.
   *
   * @param pin the PIN
   * @param salt the Base64 salt
   * @return the Base64 hash
   */
  public static String hash(String pin, String salt) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      digest.update(Base64.getDecoder().decode(salt));
      byte[] hashed = digest.digest(pin.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(hashed);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 is required on every Java platform", e);
    }
  }

  /**
   * Checks a PIN against a stored hash in constant time.
   *
   * @param pin the PIN entered
   * @param salt the stored salt
   * @param expectedHash the stored hash
   * @return true when the PIN is correct
   */
  public static boolean matches(String pin, String salt, String expectedHash) {
    byte[] actual = hash(pin, salt).getBytes(StandardCharsets.US_ASCII);
    byte[] expected = expectedHash.getBytes(StandardCharsets.US_ASCII);
    return MessageDigest.isEqual(actual, expected);
  }
}
