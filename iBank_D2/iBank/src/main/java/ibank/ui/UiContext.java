package ibank.ui;

import ibank.atm.AtmService;
import ibank.device.ReceiptPrinter;
import ibank.i18n.I18n;
import java.time.Clock;
import java.util.Map;

/**
 * The objects that every screen needs, passed from screen to screen.
 */
public final class UiContext {

  private final AtmService service;
  private final I18n i18n;
  private final ReceiptPrinter printer;
  private final Map<String, String> demoCards;
  private final Clock clock;
  private Navigator navigator;
  private Theme theme = Theme.STANDARD;

  /**
   * Creates the context.
   *
   * @param service the ABM service
   * @param i18n the text supplier
   * @param printer the receipt printer
   * @param demoCards the simulated cards that can be inserted, mapped to their holders' names
   * @param clock the clock used for receipt times
   */
  public UiContext(AtmService service, I18n i18n, ReceiptPrinter printer,
      Map<String, String> demoCards, Clock clock) {
    this.service = service;
    this.i18n = i18n;
    this.printer = printer;
    this.demoCards = demoCards;
    this.clock = clock;
  }

  /**
   * Connects the context to the window that shows the screens.
   *
   * @param windowNavigator the navigator
   */
  public void attach(Navigator windowNavigator) {
    this.navigator = windowNavigator;
  }

  public AtmService service() {
    return service;
  }

  public I18n i18n() {
    return i18n;
  }

  public ReceiptPrinter printer() {
    return printer;
  }

  public Map<String, String> demoCards() {
    return demoCards;
  }

  public Clock clock() {
    return clock;
  }

  public Navigator navigator() {
    return navigator;
  }

  public Theme theme() {
    return theme;
  }

  /** Switches between the standard and the high contrast theme. */
  public void toggleTheme() {
    theme = theme.toggled();
  }

  /**
   * Looks up a text in the current language.
   *
   * @param key the message key
   * @param arguments values for the placeholders
   * @return the text
   */
  public String text(String key, Object... arguments) {
    return i18n.text(key, arguments);
  }
}
