package ibank.ui;

import ibank.atm.AmountParser;
import ibank.domain.Account;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Deposits a cheque scanned by the envelope free slot.
 */
public final class DepositChequeScreen extends Screen {

  private static final long serialVersionUID = 1L;

  private final Chooser<Account> account = accountChooser();
  private final JTextField amount = new JTextField(10);

  /**
   * Creates the screen.
   *
   * @param context the shared context
   */
  public DepositChequeScreen(UiContext context) {
    super(context, "depositCheque.title");
    amount.addActionListener(event -> deposit());
    JPanel content = new JPanel(new BorderLayout(16, 16));
    content.add(new FormPanel(theme())
        .addRow(text("common.toAccount"), account)
        .addRow(text("depositCheque.amount"), amount), BorderLayout.NORTH);
    content.add(Ui.message(text("depositCheque.note"), theme()), BorderLayout.CENTER);
    setContent(content);
    action("depositCheque.confirm", this::deposit);
    action("common.back", this::showMenu);
    focusOnShow(amount);
  }

  private void deposit() {
    perform(() -> showResult(context().service()
        .depositCheque(account.selected().number(), AmountParser.parse(amount.getText()))));
  }
}
