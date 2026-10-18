package ibank.i18n;

/**
 * A value whose name is shown to clients in their chosen language.
 */
public interface Localizable {

  /**
   * Returns the key of this value's name in the message bundles.
   *
   * @return the message key
   */
  String messageKey();
}
