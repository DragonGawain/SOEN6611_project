package ibank.ui;

import ibank.atm.Session;
import ibank.domain.Account;
import ibank.error.BankingException;
import java.time.LocalDateTime;
import java.util.List;
import javax.swing.JButton;

/**
 * Shows the balance of every account, read from the bank, and the cash left for today.
 */
public final class BalanceScreen extends Screen {

  private static final long serialVersionUID = 1L;

  private final FormPanel form = new FormPanel(theme());
  private final JButton receipt;
  private transient List<Account> accounts;

  /**
   * Creates the screen.
   *
   * @param context the shared context
   */
  public BalanceScreen(UiContext context) {
    super(context, "balance.title");
    setContent(form);
    receipt = action("common.receipt", this::printReceipt);
    receipt.setEnabled(false);
    action("common.back", this::showMenu);
    perform(this::load);
  }

  private void load() throws BankingException {
    accounts = context().service().balances();
    for (Account account : accounts) {
      form.addRow(text(account.type().messageKey()) + " " + account.maskedNumber(),
          Ui.label(context().i18n().money(account.balance()), theme()));
    }
    form.addRow(text("withdraw.limit"),
        Ui.label(context().i18n().money(context().service().remainingDailyLimit()), theme()));
    receipt.setEnabled(true);
  }

  private void printReceipt() {
    Session session = context().service().session();
    Ui.showReceipt(this, text("receipt.title"), context().printer().balances(accounts,
        session.maskedCard(), LocalDateTime.now(context().clock())));
  }
}
