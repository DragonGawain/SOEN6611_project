package ibank.ui;

import ibank.error.BankingException;

/**
 * A user action that may fail with an expected iBank exception.
 */
@FunctionalInterface
public interface UiAction {

  /**
   * Carries out the action.
   *
   * @throws BankingException if the action is refused; the screen shows the reason to the client
   */
  void perform() throws BankingException;
}
