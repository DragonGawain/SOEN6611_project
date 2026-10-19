package ibank.ui;

import ibank.atm.AmountParser;
import ibank.domain.Account;
import ibank.domain.Payee;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Pays a bill to a payee the client registered in online banking.
 */
public final class PayBillScreen extends Screen {

  private static final long serialVersionUID = 1L;

  private final Chooser<Payee> payee;
  private final Chooser<Account> from = accountChooser();
  private final JTextField amount = new JTextField(10);

  /**
   * Creates the screen.
   *
   * @param context the shared context
   */
  public PayBillScreen(UiContext context) {
    super(context, "payBill.title");
    payee = new Chooser<>(context.service().session().client().payees(), Payee::name);
    amount.addActionListener(event -> pay());
    JPanel content = new JPanel(new BorderLayout(16, 16));
    content.add(new FormPanel(theme())
        .addRow(text("payBill.payee"), payee)
        .addRow(text("common.fromAccount"), from)
        .addRow(text("common.amount"), amount), BorderLayout.NORTH);
    setContent(content);
    JButton pay = action("payBill.confirm", this::pay);
    action("common.back", this::showMenu);
    if (payee.getItemCount() == 0) {
      pay.setEnabled(false);
      content.add(Ui.message(text("payBill.none"), theme()), BorderLayout.CENTER);
    }
    focusOnShow(amount);
  }

  private void pay() {
    perform(() -> showResult(context().service().payBill(from.selected().number(),
        payee.selected().id(), AmountParser.parse(amount.getText()))));
  }
}
