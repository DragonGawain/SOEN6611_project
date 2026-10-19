package ibank.ui;

import ibank.domain.Account;
import ibank.domain.CashBundle;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 * Deposits notes through the envelope free slot. The client enters how many notes of each kind
 * the slot counted.
 */
public final class DepositCashScreen extends Screen {

  private static final long serialVersionUID = 1L;

  private final Chooser<Account> account = accountChooser();
  private final transient Map<Integer, JSpinner> noteCounts = new LinkedHashMap<>();
  private final JLabel total = Ui.label(" ", theme());

  /**
   * Creates the screen.
   *
   * @param context the shared context
   */
  public DepositCashScreen(UiContext context) {
    super(context, "depositCash.title");
    FormPanel form = new FormPanel(theme()).addRow(text("common.toAccount"), account);
    for (int denomination : CashBundle.DENOMINATIONS) {
      JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, 0, CashBundle.MAX_NOTES, 1));
      spinner.addChangeListener(event -> updateTotal());
      noteCounts.put(denomination, spinner);
      form.addRow(text("depositCash.notes", denomination), spinner);
    }
    form.addRow(text("depositCash.total"), total);
    setContent(form);
    action("depositCash.confirm", this::deposit);
    action("common.back", this::showMenu);
    updateTotal();
    focusOnShow(account);
  }

  private Map<Integer, Integer> counts() {
    Map<Integer, Integer> counts = new LinkedHashMap<>();
    noteCounts.forEach((denomination, spinner) ->
        counts.put(denomination, (Integer) spinner.getValue()));
    return counts;
  }

  private void updateTotal() {
    total.setText(context().i18n().money(CashBundle.valueOf(counts())));
  }

  private void deposit() {
    perform(() -> showResult(context().service()
        .depositCash(account.selected().number(), CashBundle.of(counts()))));
  }
}
