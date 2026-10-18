package ibank.ui;

/**
 * Switches the screen shown on the ABM display.
 */
public interface Navigator {

  /**
   * Replaces the current screen.
   *
   * @param screen the screen to show
   */
  void display(Screen screen);
}
