package ibank.domain;

import ibank.i18n.Localizable;

/**
 * The kinds of account that the ABM can operate on.
 */
public enum AccountType implements Localizable {
  CHEQUING("account.chequing"),
  SAVINGS("account.savings");

  private final String messageKey;

  AccountType(String messageKey) {
    this.messageKey = messageKey;
  }

  @Override
  public String messageKey() {
    return messageKey;
  }
}
