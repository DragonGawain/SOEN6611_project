package ibank.ui;

import ibank.domain.Masking;
import ibank.i18n.Language;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

/**
 * The first screen: choose a language, insert a card or start a cardless withdrawal.
 */
public final class WelcomeScreen extends Screen {

  private static final long serialVersionUID = 1L;

  /**
   * Creates the screen.
   *
   * @param context the shared context
   * @param noticeKey the key of a notice to show, such as a goodbye message; null for none
   */
  public WelcomeScreen(UiContext context, String noticeKey) {
    super(context, "welcome.title");
    JPanel content = new JPanel();
    content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
    if (noticeKey != null) {
      content.add(Ui.message(text(noticeKey), theme()));
    }
    content.add(Ui.message(text("welcome.prompt"), theme()));
    JPanel languages = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 8));
    languages.setOpaque(false);
    for (Language language : Language.values()) {
      languages.add(Ui.button(language.nativeName(), () -> switchLanguage(language), theme()));
    }
    content.add(languages);
    Map<String, String> cards = context.demoCards();
    Chooser<String> card = new Chooser<>(new ArrayList<>(cards.keySet()),
        number -> cards.get(number) + "  " + Masking.lastFour(number));
    content.add(new FormPanel(theme()).addRow(text("welcome.card"), card));
    setContent(content);
    action("welcome.insert", () -> goTo(new PinScreen(context, card.selected())));
    action("welcome.cardless", () -> goTo(new CardlessScreen(context)));
    action("welcome.contrast", this::toggleContrast);
    focusOnShow(card);
  }

  private void switchLanguage(Language language) {
    context().i18n().setLanguage(language);
    goTo(new WelcomeScreen(context(), null));
  }

  private void toggleContrast() {
    context().toggleTheme();
    goTo(new WelcomeScreen(context(), null));
  }
}
