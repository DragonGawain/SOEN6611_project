package ibank.ui;

import java.awt.GridLayout;
import java.util.function.Supplier;
import javax.swing.JPanel;

/**
 * The list of transactions available once the PIN is accepted.
 */
public final class MainMenuScreen extends Screen {

  private static final long serialVersionUID = 1L;
  private static final int GAP = 16;

  /**
   * Creates the screen.
   *
   * @param context the shared context
   */
  public MainMenuScreen(UiContext context) {
    super(context, "menu.title", context.service().session().client().name());
    JPanel options = new JPanel(new GridLayout(4, 2, GAP, GAP));
    addOption(options, "menu.withdraw", () -> new WithdrawScreen(context));
    addOption(options, "menu.depositCash", () -> new DepositCashScreen(context));
    addOption(options, "menu.depositCheque", () -> new DepositChequeScreen(context));
    addOption(options, "menu.transfer", () -> new TransferScreen(context));
    addOption(options, "menu.payBill", () -> new PayBillScreen(context));
    addOption(options, "menu.balance", () -> new BalanceScreen(context));
    addOption(options, "menu.changePin", () -> new ChangePinScreen(context));
    setContent(options);
    action("menu.finish", this::finish);
  }

  private void addOption(JPanel options, String key, Supplier<Screen> next) {
    options.add(Ui.button(text(key), () -> goTo(next.get()), theme()));
  }
}
