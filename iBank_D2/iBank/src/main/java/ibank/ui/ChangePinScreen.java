package ibank.ui;

import ibank.error.AuthenticationException;
import ibank.error.BankingException;
import javax.swing.JPasswordField;

/**
 * Changes the PIN of the inserted card.
 */
public final class ChangePinScreen extends Screen {

  private static final long serialVersionUID = 1L;
  private static final int PIN_COLUMNS = 6;

  private final JPasswordField current = new JPasswordField(PIN_COLUMNS);
  private final JPasswordField fresh = new JPasswordField(PIN_COLUMNS);
  private final JPasswordField confirmation = new JPasswordField(PIN_COLUMNS);

  /**
   * Creates the screen.
   *
   * @param context the shared context
   */
  public ChangePinScreen(UiContext context) {
    super(context, "changePin.title");
    confirmation.addActionListener(event -> change());
    setContent(new FormPanel(theme())
        .addRow(text("changePin.current"), current)
        .addRow(text("changePin.new"), fresh)
        .addRow(text("changePin.confirm"), confirmation));
    action("changePin.submit", this::change);
    action("common.back", this::showMenu);
    focusOnShow(current);
  }

  private void change() {
    final String currentPin = new String(current.getPassword());
    final String newPin = new String(fresh.getPassword());
    final String repeated = new String(confirmation.getPassword());
    current.setText("");
    fresh.setText("");
    confirmation.setText("");
    perform(() -> submit(currentPin, newPin, repeated));
  }

  private void submit(String currentPin, String newPin, String repeated) throws BankingException {
    try {
      context().service().changePin(currentPin, newPin, repeated);
      goTo(ResultScreen.forMessage(context(), text("result.pinChanged")));
    } catch (AuthenticationException e) {
      if (!e.isCardBlocked()) {
        throw e;
      }
      retainCard();
    }
  }
}
