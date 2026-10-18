package ibank.ui;

import ibank.domain.TransactionRecord;
import ibank.i18n.I18n;

/**
 * Confirms a completed transaction and offers a receipt (the optional Print receipt use case).
 */
public final class ResultScreen extends Screen {

  private static final long serialVersionUID = 1L;

  private final String receipt;

  private ResultScreen(UiContext context, String message, String receipt) {
    super(context, "result.title");
    this.receipt = receipt;
    setContent(Ui.message(message, theme()));
    if (receipt != null) {
      action("common.receipt", this::printReceipt);
    }
    if (context.service().hasSession()) {
      action("result.another", this::showMenu);
    }
    action("menu.finish", this::finish);
  }

  /**
   * Creates the confirmation of a transaction.
   *
   * @param context the shared context
   * @param record the completed transaction
   * @return the screen
   */
  public static ResultScreen forRecord(UiContext context, TransactionRecord record) {
    I18n i18n = context.i18n();
    String message = i18n.text(record.type().resultKey(), record.amount(), record.balanceAfter(),
        record.counterparty());
    String cardLabel = context.service().hasSession()
        ? context.service().session().maskedCard()
        : i18n.text("receipt.noCard");
    return new ResultScreen(context, message, context.printer().transaction(record, cardLabel));
  }

  /**
   * Creates a confirmation with no receipt.
   *
   * @param context the shared context
   * @param message the confirmation text
   * @return the screen
   */
  public static ResultScreen forMessage(UiContext context, String message) {
    return new ResultScreen(context, message, null);
  }

  private void printReceipt() {
    Ui.showReceipt(this, text("receipt.title"), receipt);
  }
}
