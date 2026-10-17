package ibank.host;

import ibank.domain.Account;
import ibank.domain.AccountType;
import ibank.domain.Card;
import ibank.domain.CardlessRequest;
import ibank.domain.Client;
import ibank.domain.Money;
import ibank.domain.Payee;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Representative clients, cards and accounts used by the demonstration and by the tests.
 */
public final class SampleData {

  /** Eastern Time, the time zone of the Montreal branch. */
  public static final ZoneId ZONE = ZoneId.of("America/Toronto");

  public static final String AMELIE_CARD = "4520880012341234";
  public static final String AMELIE_PIN = "2468";
  public static final String AMELIE_CHEQUING = "5012345";
  public static final String AMELIE_SAVINGS = "5012346";
  public static final String JORDAN_CARD = "4520880056785678";
  public static final String JORDAN_PIN = "1357";
  public static final String JORDAN_CHEQUING = "6098765";
  public static final String JORDAN_SAVINGS = "6098766";
  public static final String PRIYA_CARD = "4520880099990000";
  public static final String CARDLESS_CODE = "482915";

  private SampleData() {
  }

  /**
   * Builds a host loaded with the sample clients.
   *
   * @param clock the clock for limits, expiry and timestamps
   * @return the host
   */
  public static InMemoryBankHost createHost(Clock clock) {
    BankRecords records = new BankRecords(clock);
    addAmelie(records, clock);
    addJordan(records);
    addPriya(records);
    return new InMemoryBankHost(records, new DailyWithdrawalTracker(clock),
        new LargeCashMonitor(clock), clock);
  }

  /** Returns the demonstration cards, mapped to their holders' names, in display order. */
  public static Map<String, String> demoCards() {
    Map<String, String> cards = new LinkedHashMap<>();
    cards.put(AMELIE_CARD, "Amélie Tremblay");
    cards.put(JORDAN_CARD, "Jordan Lee");
    cards.put(PRIYA_CARD, "Priya Singh");
    return Collections.unmodifiableMap(cards);
  }

  private static void addAmelie(BankRecords records, Clock clock) {
    Client client = new Client("C001", "Amélie Tremblay");
    client.addAccount(new Account(AMELIE_CHEQUING, AccountType.CHEQUING, Money.ofDollars(2500)));
    client.addAccount(new Account(AMELIE_SAVINGS, AccountType.SAVINGS, Money.ofDollars(15000)));
    client.addPayee(new Payee("P01", "Hydro-Québec"));
    client.addPayee(new Payee("P02", "Bell Canada"));
    records.addClient(client);
    records.addCard(card(AMELIE_CARD, client.id(), AMELIE_PIN));
    LocalDateTime expiry = LocalDateTime.now(clock).plusMinutes(30);
    records.addCardlessRequest(new CardlessRequest(CARDLESS_CODE, client.id(), AMELIE_CHEQUING,
        Money.ofDollars(100), expiry));
  }

  private static void addJordan(BankRecords records) {
    Client client = new Client("C002", "Jordan Lee");
    client.addAccount(new Account(JORDAN_CHEQUING, AccountType.CHEQUING, Money.ofCents(4550)));
    client.addAccount(new Account(JORDAN_SAVINGS, AccountType.SAVINGS, Money.ofDollars(300)));
    client.addPayee(new Payee("P03", "Vidéotron"));
    records.addClient(client);
    records.addCard(card(JORDAN_CARD, client.id(), JORDAN_PIN));
  }

  private static void addPriya(BankRecords records) {
    Client client = new Client("C003", "Priya Singh");
    client.addAccount(new Account("7011111", AccountType.CHEQUING, Money.ofDollars(800)));
    records.addClient(client);
    Card card = card(PRIYA_CARD, client.id(), "9024");
    card.block();
    records.addCard(card);
  }

  private static Card card(String number, String clientId, String pin) {
    String salt = PinHasher.newSalt();
    return new Card(number, clientId, salt, PinHasher.hash(pin, salt));
  }
}
