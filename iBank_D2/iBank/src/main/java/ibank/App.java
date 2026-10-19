package ibank;

import ibank.atm.AtmService;
import ibank.device.CashDispenser;
import ibank.device.ReceiptPrinter;
import ibank.host.InMemoryBankHost;
import ibank.host.SampleData;
import ibank.i18n.I18n;
import ibank.i18n.Language;
import ibank.ui.AtmFrame;
import ibank.ui.UiContext;
import java.time.Clock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Starts the iBank ABM simulator.
 */
public final class App {

  private static final Logger LOG = Logger.getLogger(App.class.getName());
  private static final int TWENTIES_LOADED = 200;
  private static final int FIFTIES_LOADED = 100;

  private App() {
  }

  /**
   * Opens the ABM window.
   *
   * @param args ignored
   */
  public static void main(String[] args) {
    SwingUtilities.invokeLater(App::launch);
  }

  private static void launch() {
    useCrossPlatformLookAndFeel();
    Clock clock = Clock.system(SampleData.ZONE);
    InMemoryBankHost host = SampleData.createHost(clock);
    CashDispenser dispenser = new CashDispenser(TWENTIES_LOADED, FIFTIES_LOADED);
    I18n i18n = new I18n(Language.ENGLISH);
    UiContext context = new UiContext(new AtmService(host, dispenser), i18n,
        new ReceiptPrinter(i18n), SampleData.demoCards(), clock);
    new AtmFrame(host, dispenser).start(context);
  }

  private static void useCrossPlatformLookAndFeel() {
    try {
      UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    } catch (ReflectiveOperationException | UnsupportedLookAndFeelException e) {
      LOG.log(Level.WARNING, "Keeping the default look and feel", e);
    }
  }
}
