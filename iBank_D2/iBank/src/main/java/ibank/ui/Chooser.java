package ibank.ui;

import java.util.List;
import java.util.Vector;
import java.util.function.Function;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;

/**
 * A drop down list that shows each item through a label function, reused for accounts, payees
 * and cards.
 *
 * @param <T> the type of the items
 */
public final class Chooser<T> extends JComboBox<T> {

  private static final long serialVersionUID = 1L;

  /**
   * Creates a chooser.
   *
   * @param items the items, in display order
   * @param labeller turns an item into the text shown
   */
  public Chooser(List<T> items, Function<T, String> labeller) {
    super(new DefaultComboBoxModel<>(new Vector<>(items)));
    DefaultListCellRenderer delegate = new DefaultListCellRenderer();
    setRenderer((list, value, index, selected, focused) -> delegate.getListCellRendererComponent(
        list, value == null ? "" : labeller.apply(value), index, selected, focused));
  }

  /** Returns the selected item, or null when the list is empty. */
  public T selected() {
    return getItemAt(getSelectedIndex());
  }
}
