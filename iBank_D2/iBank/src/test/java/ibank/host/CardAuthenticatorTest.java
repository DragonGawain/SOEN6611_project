package ibank.host;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ibank.domain.Card;
import ibank.error.AuthenticationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CardAuthenticatorTest {

  private static final String NUMBER = "4520880012341234";

  private final CardAuthenticator authenticator = new CardAuthenticator();
  private Card card;

  @BeforeEach
  void registerCard() {
    String salt = PinHasher.newSalt();
    card = new Card(NUMBER, "C001", salt, PinHasher.hash("2468", salt));
    authenticator.register(card);
  }

  @Test
  void theRightPinIsAcceptedAndThePinIsNotStoredInPlainText() throws Exception {
    assertEquals(card, authenticator.authenticate(NUMBER, "2468"));
    assertNotEquals("2468", card.pinHash());
  }

  @Test
  void theThirdWrongPinBlocksTheCard() {
    AuthenticationException first = assertThrows(AuthenticationException.class,
        () -> authenticator.authenticate(NUMBER, "0000"));
    assertEquals(2, first.arguments()[0]);
    assertThrows(AuthenticationException.class, () -> authenticator.authenticate(NUMBER, "1111"));
    AuthenticationException third = assertThrows(AuthenticationException.class,
        () -> authenticator.authenticate(NUMBER, "2222"));
    assertTrue(third.isCardBlocked());
    AuthenticationException afterwards = assertThrows(AuthenticationException.class,
        () -> authenticator.authenticate(NUMBER, "2468"));
    assertTrue(afterwards.isCardBlocked());
  }

  @Test
  void correctPinResetsTheCounter() throws Exception {
    assertThrows(AuthenticationException.class, () -> authenticator.authenticate(NUMBER, "0000"));
    assertThrows(AuthenticationException.class, () -> authenticator.authenticate(NUMBER, "0000"));
    authenticator.authenticate(NUMBER, "2468");
    AuthenticationException e = assertThrows(AuthenticationException.class,
        () -> authenticator.authenticate(NUMBER, "0000"));
    assertFalse(e.isCardBlocked());
  }

  @Test
  void unknownCardIsRejected() {
    AuthenticationException e = assertThrows(AuthenticationException.class,
        () -> authenticator.authenticate("0000000000000000", "2468"));
    assertEquals("error.card.unknown", e.messageKey());
  }

  @Test
  void afterPinChangeOnlyTheNewPinWorks() throws Exception {
    authenticator.changePin(NUMBER, "2468", "8023", "8023");
    assertEquals(card, authenticator.authenticate(NUMBER, "8023"));
    assertThrows(AuthenticationException.class, () -> authenticator.authenticate(NUMBER, "2468"));
  }
}
