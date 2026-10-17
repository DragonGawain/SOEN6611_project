package ibank.device;

import ibank.domain.Account;
import ibank.domain.Masking;
import ibank.domain.TransactionRecord;
import ibank.i18n.I18n;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Formats the text of paper receipts. Card and account numbers are always masked.
 */
public final class ReceiptPrinter {

  private static final DateTimeFormatter TIME_FORMAT =
      DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss", Locale.ROOT);
  private static final String RULE = "================================";

  private final I18n i18n;

  /**
   * Creates a printer.
   *
   * @param i18n the text supplier for the receipt labels
   */
  public ReceiptPrinter(I18n i18n) {
    this.i18n = i18n;
  }

  /**
   * Formats the receipt of one transaction.
   *
   * @param record the transaction
   * @param cardLabel the masked card number, or a label for a cardless withdrawal
   * @return the receipt text
   */
  public String transaction(TransactionRecord record, String cardLabel) {
    StringBuilder receipt = header(cardLabel, record.time());
    line(receipt, "receipt.reference", record.reference());
    line(receipt, "receipt.type", i18n.text(record.type().messageKey()));
    line(receipt, "receipt.account", i18n.text(record.accountType().messageKey()) + " "
        + Masking.lastFour(record.accountNumber()));
    if (!record.counterparty().isEmpty()) {
      line(receipt, "receipt.counterparty", record.counterparty());
    }
    line(receipt, "receipt.amount", i18n.money(record.amount()));
    line(receipt, "receipt.balance", i18n.money(record.balanceAfter()));
    return footer(receipt);
  }

  /**
   * Formats a balance receipt.
   *
   * @param accounts the client's accounts
   * @param cardLabel the masked card number
   * @param time when the balances were read
   * @return the receipt text
   */
  public String balances(List<Account> accounts, String cardLabel, LocalDateTime time) {
    StringBuilder receipt = header(cardLabel, time);
    for (Account account : accounts) {
      line(receipt, account.type().messageKey(),
          account.maskedNumber() + "  " + i18n.money(account.balance()));
    }
    return footer(receipt);
  }

  private StringBuilder header(String cardLabel, LocalDateTime time) {
    StringBuilder receipt = new StringBuilder();
    receipt.append(i18n.text("receipt.bank")).append('\n').append(RULE).append('\n');
    line(receipt, "receipt.date", TIME_FORMAT.format(time));
    line(receipt, "receipt.card", cardLabel);
    return receipt;
  }

  private void line(StringBuilder receipt, String labelKey, String value) {
    receipt.append(String.format(Locale.ROOT, "%-14s %s", i18n.text(labelKey), value))
        .append('\n');
  }

  private String footer(StringBuilder receipt) {
    return receipt.append(RULE).append('\n').append(i18n.text("receipt.thanks")).toString();
  }
}
