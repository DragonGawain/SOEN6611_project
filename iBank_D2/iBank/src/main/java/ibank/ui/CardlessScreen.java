package ibank.ui;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Withdraws cash with a one time code created in the bank's mobile app; no card is needed.
 */
public final class CardlessScreen extends Screen {

  private static final long serialVersionUID = 1L;
  private static final int CODE_COLUMNS = 8;

  private final JTextField code = new JTextField(CODE_COLUMNS);

  /**
   * Creates the screen.
   *
   * @param context the shared context
   */
  public CardlessScreen(UiContext context) {
    super(context, "cardless.title");
    code.addActionListener(event -> submit());
    JPanel content = new JPanel(new BorderLayout(16, 16));
    content.add(Ui.message(text("cardless.help"), theme()), BorderLayout.NORTH);
    content.add(new FormPanel(theme()).addRow(text("cardless.code"), code), BorderLayout.CENTER);
    content.add(Ui.centred(new Keypad(code, context)), BorderLayout.SOUTH);
    setContent(content);
    action("common.ok", this::submit);
    action("common.cancel", () -> goTo(new WelcomeScreen(context, null)));
    focusOnShow(code);
  }

  private void submit() {
    String entered = code.getText().trim();
    code.setText("");
    perform(() -> showResult(context().service().withdrawWithCode(entered)));
  }
}
