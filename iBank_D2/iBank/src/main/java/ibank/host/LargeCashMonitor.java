package ibank.host;

import ibank.domain.Money;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Flags cash deposits that add up to $10,000 or more from one client within 24 hours, which the
 * bank must report to FINTRAC as a large cash transaction under the PCMLTFA.
 */
public final class LargeCashMonitor {

  /** The amount at which a report is due. */
  public static final Money THRESHOLD = Money.ofDollars(10000);

  private static final Duration WINDOW = Duration.ofHours(24);

  private final Clock clock;
  private final Map<String, List<Entry>> recentDeposits = new HashMap<>();
  private final List<Entry> reports = new ArrayList<>();

  /**
   * Creates a monitor.
   *
   * @param clock the clock used to time deposits
   */
  public LargeCashMonitor(Clock clock) {
    this.clock = clock;
  }

  /**
   * Records a cash deposit.
   *
   * @param clientId the client
   * @param amount the cash received
   * @return true when this deposit makes a report due
   */
  public boolean record(String clientId, Money amount) {
    LocalDateTime now = LocalDateTime.now(clock);
    List<Entry> deposits = recentDeposits.computeIfAbsent(clientId, id -> new ArrayList<>());
    deposits.removeIf(entry -> entry.time().isBefore(now.minus(WINDOW)));
    deposits.add(new Entry(clientId, amount, now));
    Money total = Money.ZERO;
    for (Entry entry : deposits) {
      total = total.plus(entry.amount());
    }
    if (total.compareTo(THRESHOLD) < 0) {
      return false;
    }
    reports.add(new Entry(clientId, total, now));
    deposits.clear();
    return true;
  }

  /** Returns the reports raised so far, oldest first. */
  public List<Entry> reports() {
    return Collections.unmodifiableList(reports);
  }

  /**
   * An amount of cash received from a client at a given time.
   */
  public static final class Entry {

    private final String clientId;
    private final Money amount;
    private final LocalDateTime time;

    Entry(String clientId, Money amount, LocalDateTime time) {
      this.clientId = clientId;
      this.amount = amount;
      this.time = time;
    }

    public String clientId() {
      return clientId;
    }

    public Money amount() {
      return amount;
    }

    public LocalDateTime time() {
      return time;
    }
  }
}
