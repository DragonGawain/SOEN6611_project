package ibank.ui;

import ibank.atm.AmountParser;
import ibank.domain.Account;
import ibank.domain.Money;
import ibank.error.BankingException;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Withdraws cash, either with a fast cash button or a typed amount.
 */
public final class WithdrawScreen extends Screen {

  private static final long serialVersionUID = 1L;
  private static final int[] FAST_CASH_DOLLARS = {20, 40, 60, 100, 200};
  private static final int GAP = 12;

  private final Chooser<Account> account = accountChooser();
  private final JTextField otherAmount = new JTextField(8);

  /**
   * Creates the screen.
   *
   * @param context the shared context
   */
  public WithdrawScreen(UiContext context) {
    super(context, "withdraw.title");
    JPanel fastCash = new JPanel(new GridLayout(1, FAST_CASH_DOLLARS.length, GAP, GAP));
    fastCash.setOpaque(false);
    for (int dollars : FAST_CASH_DOLLARS) {
      Money amount = Money.ofDollars(dollars);
      String label = text("withdraw.fastAmount", dollars);
      fastCash.add(Ui.button(label, () -> withdraw(amount), theme()));
    }
    JLabel limit = Ui.label(" ", theme());
    otherAmount.addActionListener(event -> withdrawTyped());
    setContent(new FormPanel(theme())
        .addRow(text("common.fromAccount"), account)
        .addRow(text("withdraw.fast"), fastCash)
        .addRow(text("withdraw.other"), otherAmount)
        .addRow(text("withdraw.limit"), limit)
        .addRow(text("withdraw.notes"), Ui.message(text("withdraw.notesHelp"), theme())));
    action("withdraw.confirm", this::withdrawTyped);
    action("common.back", this::showMenu);
    perform(() -> limit.setText(context.i18n().money(context.service().remainingDailyLimit())));
    focusOnShow(otherAmount);
  }

  private void withdraw(Money amount) {
    perform(() -> complete(amount));
  }

  private void withdrawTyped() {
    perform(() -> complete(AmountParser.parse(otherAmount.getText())));
  }

  private void complete(Money amount) throws BankingException {
    showResult(context().service().withdraw(account.selected().number(), amount));
  }
}
