package ibank.ui;

import java.awt.Color;
import java.awt.Font;

/**
 * Colours and font sizes of the display, in a standard and a high contrast variant.
 */
public final class Theme {

  /** Maroon on white, the standard look. */
  public static final Theme STANDARD = new Theme(Color.WHITE, new Color(0x1F1F1F),
      new Color(0x912338), Color.WHITE, new Color(0xB00020), 0);

  /** Yellow and white on black with larger text, for clients with low vision. */
  public static final Theme HIGH_CONTRAST = new Theme(Color.BLACK, Color.WHITE,
      new Color(0xFFD400), Color.BLACK, new Color(0xFF8A8A), 6);

  private static final int TITLE_SIZE = 30;
  private static final int BODY_SIZE = 20;

  private final Color background;
  private final Color foreground;
  private final Color accent;
  private final Color onAccent;
  private final Color error;
  private final Font titleFont;
  private final Font bodyFont;
  private final Font buttonFont;

  private Theme(Color background, Color foreground, Color accent, Color onAccent, Color error,
      int extraSize) {
    this.background = background;
    this.foreground = foreground;
    this.accent = accent;
    this.onAccent = onAccent;
    this.error = error;
    this.titleFont = new Font(Font.SANS_SERIF, Font.BOLD, TITLE_SIZE + extraSize);
    this.bodyFont = new Font(Font.SANS_SERIF, Font.PLAIN, BODY_SIZE + extraSize);
    this.buttonFont = new Font(Font.SANS_SERIF, Font.BOLD, BODY_SIZE + extraSize);
  }

  public Color background() {
    return background;
  }

  public Color foreground() {
    return foreground;
  }

  public Color accent() {
    return accent;
  }

  /** Returns the text colour used on top of the accent colour. */
  public Color onAccent() {
    return onAccent;
  }

  public Color error() {
    return error;
  }

  public Font titleFont() {
    return titleFont;
  }

  public Font bodyFont() {
    return bodyFont;
  }

  public Font buttonFont() {
    return buttonFont;
  }

  /** Returns the other variant of the theme. */
  public Theme toggled() {
    return this == HIGH_CONTRAST ? STANDARD : HIGH_CONTRAST;
  }
}
