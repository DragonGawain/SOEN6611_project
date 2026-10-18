package ibank.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A two column form of labels and fields. Each label is linked to its field for screen readers.
 */
public final class FormPanel extends JPanel {

  private static final long serialVersionUID = 1L;
  private static final int SPACING = 8;

  private final transient Theme theme;
  private int rows;

  /**
   * Creates an empty form.
   *
   * @param theme the theme for labels and fields
   */
  public FormPanel(Theme theme) {
    super(new GridBagLayout());
    this.theme = theme;
    setOpaque(false);
  }

  /**
   * Adds a row.
   *
   * @param labelText the label
   * @param field the field the label describes
   * @return this form, so rows can be chained
   */
  public FormPanel addRow(String labelText, JComponent field) {
    JLabel label = Ui.label(labelText, theme);
    label.setLabelFor(field);
    field.setFont(theme.bodyFont());
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.gridy = rows;
    constraints.insets = new Insets(SPACING, SPACING, SPACING, SPACING);
    constraints.anchor = GridBagConstraints.LINE_END;
    add(label, constraints);
    constraints.gridx = 1;
    constraints.anchor = GridBagConstraints.LINE_START;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.weightx = 1;
    add(field, constraints);
    rows++;
    return this;
  }
}
