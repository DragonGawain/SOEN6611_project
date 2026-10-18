package ibank.ui;

import ibank.device.CashDispenser;
import ibank.host.InMemoryBankHost;
import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.Timer;
import javax.swing.WindowConstants;

/**
 * The ABM window. It shows one screen at a time and ends an idle session after one minute.
 */
public final class AtmFrame extends JFrame implements Navigator {

  private static final long serialVersionUID = 1L;
  private static final int IDLE_MILLIS = 60000;
  private static final Dimension MINIMUM_SIZE = new Dimension(960, 720);

  private final Timer idleTimer;
  private transient UiContext context;

  /**
   * Creates the window.
   *
   * @param host the simulated host, for the simulation menu
   * @param dispenser the cash dispenser, for the simulation menu
   */
  public AtmFrame(InMemoryBankHost host, CashDispenser dispenser) {
    super("iBank ABM");
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setMinimumSize(MINIMUM_SIZE);
    JMenuBar menuBar = new JMenuBar();
    menuBar.add(new OperatorMenu(host, dispenser));
    setJMenuBar(menuBar);
    idleTimer = new Timer(IDLE_MILLIS, event -> endIdleSession());
    idleTimer.setRepeats(false);
    Toolkit.getDefaultToolkit().addAWTEventListener(event -> idleTimer.restart(),
        AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK);
  }

  /**
   * Shows the welcome screen and opens the window.
   *
   * @param uiContext the shared context
   */
  public void start(UiContext uiContext) {
    this.context = uiContext;
    uiContext.attach(this);
    display(new WelcomeScreen(uiContext, null));
    setLocationRelativeTo(null);
    setVisible(true);
  }

  @Override
  public void display(Screen screen) {
    setContentPane(screen);
    revalidate();
    repaint();
    screen.focusFirst();
    idleTimer.restart();
  }

  private void endIdleSession() {
    if (context != null && context.service().hasSession()) {
      context.service().end();
      display(new WelcomeScreen(context, "notice.timeout"));
    }
  }
}
