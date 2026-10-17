package ibank.host;

import ibank.domain.Card;
import ibank.domain.CardlessRequest;
import ibank.domain.Client;
import ibank.error.TransactionRejectedException;
import java.time.Clock;
import java.util.HashMap;
import java.util.Map;

/**
 * The bank's stored data: clients, cards and cardless withdrawal codes.
 */
public final class BankRecords {

  private final Map<String, Client> clients = new HashMap<>();
  private final CardAuthenticator authenticator = new CardAuthenticator();
  private final CardlessRegistry cardless;

  /**
   * Creates empty records.
   *
   * @param clock the clock used to check cardless code expiry
   */
  public BankRecords(Clock clock) {
    this.cardless = new CardlessRegistry(clock);
  }

  public void addClient(Client client) {
    clients.put(client.id(), client);
  }

  public void addCard(Card card) {
    authenticator.register(card);
  }

  public void addCardlessRequest(CardlessRequest request) {
    cardless.register(request);
  }

  /**
   * Finds a client.
   *
   * @param clientId the client identifier
   * @return the client
   * @throws TransactionRejectedException if no client has this identifier
   */
  public Client client(String clientId) throws TransactionRejectedException {
    Client client = clients.get(clientId);
    if (client == null) {
      throw new TransactionRejectedException("error.client.unknown");
    }
    return client;
  }

  CardAuthenticator authenticator() {
    return authenticator;
  }

  CardlessRegistry cardless() {
    return cardless;
  }
}
