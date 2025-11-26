package sanguine.view;

import sanguine.model.Card;

/**
 * represents a listener who is notified of events called in a game of Sanguine. Must be registered
 * with a {@link SanguineView} to be called.
 */
public interface SanguineEventListener {

  /**
   * called when a card is clicked. Gives the card that was clicked.
   *
   * @param card the card represented by the clicked panel
   */
  void cardClicked(Card card);

  /**
   * called when a panel representing a game tile is clicked.
   *
   * @param row the row of the tile in the game grid (0-indexed from the top)
   * @param col the column of the tile in the game grid (0-indexed from the left)
   */
  void tileClicked(int row, int col);

  /**
   * called when an input is given corresponding to attempting to place a card.
   */
  void placeCard();

  /**
   * called when an input is given corresponding to passing.
   */
  void pass();
}
