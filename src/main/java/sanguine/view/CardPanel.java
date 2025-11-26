package sanguine.view;

import sanguine.model.Card;

/**
 * represents a panel showing a {@link Card} in a view for a game of Sanguine. Clicks on this panel
 * can be detected with {@link SanguineEventListener}s registered through the {@code register}
 * method.
 */
public interface CardPanel extends SanguineEventSender, ClickSwitch {

  /**
   * returns the card that this panel represents.
   *
   * @return the card that this panel represents
   */
  Card getCard();


}
