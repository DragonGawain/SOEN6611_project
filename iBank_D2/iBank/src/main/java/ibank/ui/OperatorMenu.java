package ibank.ui;

import ibank.device.CashDispenser;
import ibank.host.InMemoryBankHost;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

/**
 * Simulation controls for demonstrations and manual tests. A real ABM has no such menu.
 */
final class OperatorMenu extends JMenu {

  private static final long serialVersionUID = 1L;

  OperatorMenu(InMemoryBankHost host, CashDispenser dispenser) {
    super("Simulation");
    JCheckBoxMenuItem online = new JCheckBoxMenuItem("Bank system online", true);
    online.addActionListener(event -> host.setOnline(online.isSelected()));
    JCheckBoxMenuItem fault = new JCheckBoxMenuItem("Cash dispenser fault", false);
    fault.addActionListener(event -> dispenser.setFaulty(fault.isSelected()));
    add(online);
    add(fault);
  }
}
