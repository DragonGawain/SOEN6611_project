package ibank.i18n;

import java.util.Locale;

/**
 * The interface languages offered by the ABM (assumption A4).
 */
public enum Language {
  ENGLISH(Locale.CANADA, "English"),
  FRENCH(Locale.CANADA_FRENCH, "Français");

  private final Locale locale;
  private final String nativeName;

  Language(Locale locale, String nativeName) {
    this.locale = locale;
    this.nativeName = nativeName;
  }

  public Locale locale() {
    return locale;
  }

  /** Returns the language's name written in that language, as shown on its button. */
  public String nativeName() {
    return nativeName;
  }
}
