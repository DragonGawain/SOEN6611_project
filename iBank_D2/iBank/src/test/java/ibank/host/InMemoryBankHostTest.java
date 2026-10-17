package ibank.host;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ibank.MutableClock;
import ibank.domain.Client;
import ibank.domain.Money;
import ibank.domain.TransactionRecord;
import ibank.error.HostUnavailableException;
import ibank.error.InsufficientFundsException;
import ibank.error.InvalidAmountException;
import ibank.error.TransactionRejectedException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class InMemoryBankHostTest {

  private InMemoryBankHost host;
  private Client amelie;

  @BeforeEach
  void loadSampleData() throws Exception {
    host = SampleData.createHost(
        new MutableClock(LocalDateTime.of(2026, 10, 19, 10, 0), SampleData.ZONE));
    amelie = host.authenticate(SampleData.AMELIE_CARD, SampleData.AMELIE_PIN);
  }

  private Money balance(String accountNumber) throws Exception {
    return amelie.account(accountNumber).balance();
  }

  @Test
  void transferMovesMoneyAndKeepsTheTotal() throws Exception {
    TransactionRecord record = host.transfer("C001", SampleData.AMELIE_SAVINGS,
        SampleData.AMELIE_CHEQUING, Money.ofDollars(500));
    assertEquals(Money.ofDollars(14500), balance(SampleData.AMELIE_SAVINGS));
    assertEquals(Money.ofDollars(3000), balance(SampleData.AMELIE_CHEQUING));
    assertEquals("****2345", record.counterparty());
  }

  @Test
  void transferToTheSameAccountIsRejected() {
    TransactionRejectedException e = assertThrows(TransactionRejectedException.class,
        () -> host.transfer("C001", SampleData.AMELIE_CHEQUING, SampleData.AMELIE_CHEQUING,
            Money.ofDollars(1)));
    assertEquals("error.transfer.same", e.messageKey());
  }

  @Test
  void billCanOnlyBePaidToRegisteredPayeeWithEnoughFunds() throws Exception {
    host.payBill("C001", SampleData.AMELIE_CHEQUING, "P01", Money.ofCents(8742));
    assertEquals(Money.ofCents(241258), balance(SampleData.AMELIE_CHEQUING));
    assertThrows(TransactionRejectedException.class,
        () -> host.payBill("C001", SampleData.AMELIE_CHEQUING, "P03", Money.ofDollars(1)));
    assertThrows(InsufficientFundsException.class,
        () -> host.payBill("C001", SampleData.AMELIE_CHEQUING, "P02", Money.ofDollars(5000)));
  }

  @Test
  void chequesUpToTheLimitAreAccepted() throws Exception {
    host.depositCheque("C001", SampleData.AMELIE_CHEQUING, InMemoryBankHost.MAX_CHEQUE);
    assertEquals(Money.ofDollars(22500), balance(SampleData.AMELIE_CHEQUING));
    assertThrows(InvalidAmountException.class, () -> host.depositCheque("C001",
        SampleData.AMELIE_CHEQUING, InMemoryBankHost.MAX_CHEQUE.plus(Money.ofCents(1))));
  }

  @Test
  void everyTransactionIsRefusedWhileTheHostIsOffline() {
    host.setOnline(false);
    Executable[] requests = {
        () -> host.authenticate(SampleData.AMELIE_CARD, SampleData.AMELIE_PIN),
        () -> host.accounts("C001"),
        () -> host.withdraw("C001", SampleData.AMELIE_CHEQUING, Money.ofDollars(20)),
        () -> host.depositCheque("C001", SampleData.AMELIE_CHEQUING, Money.ofDollars(20)),
        () -> host.transfer("C001", SampleData.AMELIE_SAVINGS, SampleData.AMELIE_CHEQUING,
            Money.ofDollars(20)),
        () -> host.payBill("C001", SampleData.AMELIE_CHEQUING, "P01", Money.ofDollars(20)),
        () -> host.findCardless(SampleData.CARDLESS_CODE),
        () -> host.changePin(SampleData.AMELIE_CARD, SampleData.AMELIE_PIN, "8023", "8023")
    };
    for (Executable request : requests) {
      assertThrows(HostUnavailableException.class, request);
    }
  }
}
