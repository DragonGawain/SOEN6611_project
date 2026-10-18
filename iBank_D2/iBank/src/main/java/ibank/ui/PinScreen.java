package ibank.ui;

import ibank.error.AuthenticationException;
import ibank.error.BankingException;
import java.awt.BorderLayout;
import java.util.Arrays;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

/**
 * Asks for the PIN of the inserted card.
 */
public final class PinScreen extends Screen {

  private static final long serialVersionUID = 1L;
  private static final int PIN_COLUMNS = 6;

  private final String cardNumber;
  private final JPasswordField pin = new JPasswordField(PIN_COLUMNS);

  /**
   * Creates the screen.
   *
   * @param context the shared context
   * @param cardNumber the card in the reader
   */
  public PinScreen(UiContext context, String cardNumber) {
    super(context, "pin.title");
    this.cardNumber = cardNumber;
    pin.addActionListener(event -> submit());
    JPanel content = new JPanel(new BorderLayout(16, 16));
    content.add(new FormPanel(theme()).addRow(text("pin.label"), pin), BorderLayout.NORTH);
    content.add(Ui.centred(new Keypad(pin, context)), BorderLayout.CENTER);
    setContent(content);
    action("common.ok", this::submit);
    action("common.cancel", () -> goTo(new WelcomeScreen(context, "notice.cardReturned")));
    focusOnShow(pin);
  }

  private void submit() {
    char[] entered = pin.getPassword();
    pin.setText("");
    perform(() -> logIn(new String(entered)));
    Arrays.fill(entered, '\0');
  }

  private void logIn(String enteredPin) throws BankingException {
    try {
      context().service().start(cardNumber, enteredPin);
      showMenu();
    } catch (AuthenticationException e) {
      if (!e.isCardBlocked()) {
        throw e;
      }
      retainCard();
    }
  }
}
