package ibank.device;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ibank.domain.Account;
import ibank.domain.AccountType;
import ibank.domain.Money;
import ibank.domain.TransactionRecord;
import ibank.domain.TransactionType;
import ibank.i18n.I18n;
import ibank.i18n.Language;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class ReceiptPrinterTest {

  @Test
  void receiptsNeverShowFullCardOrAccountNumbers() {
    Account account = new Account("5012345", AccountType.CHEQUING, Money.ofDollars(2440));
    TransactionRecord record = new TransactionRecord("IB000001", TransactionType.WITHDRAWAL,
        Money.ofDollars(60), account, "", LocalDateTime.of(2026, 10, 19, 10, 0));
    String receipt = new ReceiptPrinter(new I18n(Language.FRENCH))
        .transaction(record, "****1234");
    assertFalse(receipt.contains("5012345"));
    assertTrue(receipt.contains("****2345"));
    assertTrue(receipt.contains("Retrait"));
    assertTrue(receipt.contains("IB000001"));
  }
}
