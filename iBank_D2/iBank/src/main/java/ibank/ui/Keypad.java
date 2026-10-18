package ibank.ui;

import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

/**
 * An on screen numeric keypad that types into a field, reused for PINs and cardless codes.
 *
 * <p>The keys never take the keyboard focus, so a client can also type on the physical keypad.
 */
public final class Keypad extends JPanel {

  private static final long serialVersionUID = 1L;
  private static final int GAP = 8;

  /**
   * Creates a keypad.
   *
   * @param target the field the keys type into
   * @param context the context for texts and theme
   */
  public Keypad(JTextComponent target, UiContext context) {
    super(new GridLayout(4, 3, GAP, GAP));
    setOpaque(false);
    Theme theme = context.theme();
    for (int digit = 1; digit <= 9; digit++) {
      add(digitKey(target, String.valueOf(digit), theme));
    }
    add(key(context.text("keypad.clear"), () -> target.setText(""), theme));
    add(digitKey(target, "0", theme));
    add(key(context.text("keypad.delete"), () -> deleteLast(target), theme));
  }

  private static JButton digitKey(JTextComponent target, String digit, Theme theme) {
    return key(digit, () -> append(target, digit), theme);
  }

  private static JButton key(String label, Runnable action, Theme theme) {
    JButton button = Ui.button(label, action, theme);
    button.setFocusable(false);
    return button;
  }

  private static void append(JTextComponent target, String digit) {
    Document document = target.getDocument();
    try {
      document.insertString(document.getLength(), digit, null);
    } catch (BadLocationException e) {
      throw new IllegalStateException("The end of a document is always a valid position", e);
    }
  }

  private static void deleteLast(JTextComponent target) {
    Document document = target.getDocument();
    if (document.getLength() == 0) {
      return;
    }
    try {
      document.remove(document.getLength() - 1, 1);
    } catch (BadLocationException e) {
      throw new IllegalStateException("The last character is always a valid position", e);
    }
  }
}
