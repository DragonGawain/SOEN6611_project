package ibank.ui;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Factory methods for the themed components that all screens share.
 */
final class Ui {

  private static final int MESSAGE_COLUMNS = 42;
  private static final int RECEIPT_FONT_SIZE = 15;

  private Ui() {
  }

  static JButton button(String text, Runnable action, Theme theme) {
    JButton button = new JButton(text);
    button.setFont(theme.buttonFont());
    button.setBackground(theme.accent());
    button.setForeground(theme.onAccent());
    button.setMargin(new Insets(10, 18, 10, 18));
    button.addActionListener(event -> action.run());
    return button;
  }

  static JLabel label(String text, Theme theme) {
    JLabel label = new JLabel(text);
    label.setFont(theme.bodyFont());
    label.setForeground(theme.foreground());
    return label;
  }

  static JTextArea message(String text, Theme theme) {
    JTextArea area = new JTextArea(text, 1, MESSAGE_COLUMNS);
    area.setFont(theme.bodyFont());
    area.setForeground(theme.foreground());
    area.setLineWrap(true);
    area.setWrapStyleWord(true);
    area.setEditable(false);
    area.setFocusable(false);
    area.setOpaque(false);
    return area;
  }

  static JPanel centred(JComponent component) {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    panel.setOpaque(false);
    panel.add(component);
    return panel;
  }

  static void showReceipt(Component parent, String title, String receipt) {
    JTextArea area = new JTextArea(receipt);
    area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, RECEIPT_FONT_SIZE));
    area.setEditable(false);
    JOptionPane.showMessageDialog(parent, new JScrollPane(area), title,
        JOptionPane.PLAIN_MESSAGE);
  }
}
