package sanguine.view;

/**
 * represents elements that can be clicked in a switch fashion. objects can either be clicked
 * or unclicked, which can be set with the {@code setClick} method.
 */
public interface ClickSwitch {
  /**
   * either clicks the panel (if clicked is true) or unclicks it (if clicked is false).
   *
   * @param clicked whether the frame is clicked
   */
  void setClick(boolean clicked);

  /**
   * whether the button is clicked.
   *
   * @return whether the button is clicked
   */
  boolean clicked();
}
