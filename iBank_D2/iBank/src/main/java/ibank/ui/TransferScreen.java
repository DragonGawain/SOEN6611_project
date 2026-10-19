package ibank.ui;

import ibank.atm.AmountParser;
import ibank.domain.Account;
import javax.swing.JTextField;

/**
 * Moves money between two of the client's own accounts.
 */
public final class TransferScreen extends Screen {

  private static final long serialVersionUID = 1L;

  private final Chooser<Account> from = accountChooser();
  private final Chooser<Account> to = accountChooser();
  private final JTextField amount = new JTextField(10);

  /**
   * Creates the screen.
   *
   * @param context the shared context
   */
  public TransferScreen(UiContext context) {
    super(context, "transfer.title");
    if (to.getItemCount() > 1) {
      to.setSelectedIndex(1);
    }
    amount.addActionListener(event -> transfer());
    setContent(new FormPanel(theme())
        .addRow(text("common.fromAccount"), from)
        .addRow(text("common.toAccount"), to)
        .addRow(text("common.amount"), amount));
    action("transfer.confirm", this::transfer);
    action("common.back", this::showMenu);
    focusOnShow(amount);
  }

  private void transfer() {
    perform(() -> showResult(context().service().transfer(from.selected().number(),
        to.selected().number(), AmountParser.parse(amount.getText()))));
  }
}
