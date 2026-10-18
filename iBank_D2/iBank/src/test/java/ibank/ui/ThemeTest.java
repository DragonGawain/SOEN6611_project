package ibank.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import org.junit.jupiter.api.Test;

/**
 * Checks the colour pairs against the WCAG 2 contrast ratio of 4.5 to 1 for body text.
 */
class ThemeTest {

  private static final double MINIMUM_RATIO = 4.5;

  private static double luminance(Color color) {
    double[] channels = {color.getRed(), color.getGreen(), color.getBlue()};
    double[] weights = {0.2126, 0.7152, 0.0722};
    double total = 0;
    for (int i = 0; i < channels.length; i++) {
      double c = channels[i] / 255.0;
      double linear = c <= 0.03928 ? c / 12.92 : Math.pow((c + 0.055) / 1.055, 2.4);
      total += weights[i] * linear;
    }
    return total;
  }

  private static double ratio(Color a, Color b) {
    double lighter = Math.max(luminance(a), luminance(b));
    double darker = Math.min(luminance(a), luminance(b));
    return (lighter + 0.05) / (darker + 0.05);
  }

  @Test
  void everyTextColourIsReadableOnItsBackground() {
    for (Theme theme : new Theme[] {Theme.STANDARD, Theme.HIGH_CONTRAST}) {
      assertTrue(ratio(theme.foreground(), theme.background()) >= MINIMUM_RATIO);
      assertTrue(ratio(theme.accent(), theme.background()) >= MINIMUM_RATIO);
      assertTrue(ratio(theme.onAccent(), theme.accent()) >= MINIMUM_RATIO);
      assertTrue(ratio(theme.error(), theme.background()) >= MINIMUM_RATIO);
    }
  }
}
