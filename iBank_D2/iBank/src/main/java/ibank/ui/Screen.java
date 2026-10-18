package ibank.ui;

import ibank.domain.Account;
import ibank.domain.TransactionRecord;
import ibank.error.BankingException;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

/**
 * Base class of every ABM screen: a title, a content area, an error line and a row of actions.
 *
 * <p>{@link #perform(UiAction)} is the one place where expected failures are turned into helpful
 * messages, so no screen needs its own error handling.
 */
public abstract class Screen extends JPanel {

  private static final long serialVersionUID = 1L;
  private static final Logger LOG = Logger.getLogger(Screen.class.getName());
  private static final int GAP = 16;
  private static final int BORDER = 32;

  private final transient UiContext context;
  private final JTextArea errorArea;
  private final JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.CENTER, GAP, 0));
  private JComponent firstFocus;

  /**
   * Builds the frame of a screen.
   *
   * @param context the shared context
   * @param titleKey the key of the screen title
   * @param titleArguments values for placeholders in the title
   */
  protected Screen(UiContext context, String titleKey, Object... titleArguments) {
    super(new BorderLayout(GAP, GAP));
    this.context = context;
    Theme theme = context.theme();
    setBackground(theme.background());
    setBorder(BorderFactory.createEmptyBorder(BORDER, BORDER, BORDER, BORDER));
    JLabel title = new JLabel(context.text(titleKey, titleArguments), SwingConstants.CENTER);
    title.setFont(theme.titleFont());
    title.setForeground(theme.accent());
    add(title, BorderLayout.NORTH);
    errorArea = Ui.message(" ", theme);
    errorArea.setForeground(theme.error());
    errorArea.getAccessibleContext().setAccessibleName(context.text("a11y.error"));
    JPanel bottom = new JPanel(new BorderLayout(GAP, GAP));
    bottom.setOpaque(false);
    actionBar.setOpaque(false);
    bottom.add(errorArea, BorderLayout.NORTH);
    bottom.add(actionBar, BorderLayout.SOUTH);
    add(bottom, BorderLayout.SOUTH);
  }

  protected final UiContext context() {
    return context;
  }

  protected final Theme theme() {
    return context.theme();
  }

  protected final String text(String key, Object... arguments) {
    return context.text(key, arguments);
  }

  protected final void setContent(JComponent content) {
    content.setOpaque(false);
    add(content, BorderLayout.CENTER);
  }

  protected final JButton action(String key, Runnable action) {
    JButton button = Ui.button(text(key), action, theme());
    actionBar.add(button);
    return button;
  }

  protected final void focusOnShow(JComponent component) {
    firstFocus = component;
  }

  /** Moves the keyboard focus to the screen's main field. */
  public final void focusFirst() {
    if (firstFocus != null) {
      firstFocus.requestFocusInWindow();
    }
  }

  /**
   * Runs an action and explains any failure on the error line.
   *
   * @param action the action
   */
  protected final void perform(UiAction action) {
    errorArea.setText(" ");
    try {
      action.perform();
    } catch (BankingException e) {
      showError(context.i18n().message(e));
    } catch (RuntimeException e) {
      LOG.log(Level.SEVERE, "Unexpected failure", e);
      showError(text("error.unexpected"));
    }
  }

  protected final void showError(String message) {
    errorArea.setText(message);
    Toolkit.getDefaultToolkit().beep();
  }

  protected final void goTo(Screen next) {
    context.navigator().display(next);
  }

  protected final void showMenu() {
    goTo(new MainMenuScreen(context));
  }

  protected final void showResult(TransactionRecord record) {
    goTo(ResultScreen.forRecord(context, record));
  }

  protected final void finish() {
    context.service().end();
    goTo(new WelcomeScreen(context, "notice.goodbye"));
  }

  /** Ends the session because the card was blocked, and tells the client it was kept. */
  protected final void retainCard() {
    context.service().end();
    goTo(new WelcomeScreen(context, "notice.cardRetained"));
  }

  protected final Chooser<Account> accountChooser() {
    return new Chooser<>(context.service().accounts(), context.i18n()::describe);
  }
}
