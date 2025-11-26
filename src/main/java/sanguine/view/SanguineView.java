package sanguine.view;


import sanguine.model.Card;

/**
 * The behaviors necessary to view a game of Klondike. The view is not shown on construction, but
 * is initiated with the {@code showImage} method. Each card or tile can be clicked, only one card
 * and tile can be clicked at a time.
 */
public interface SanguineView extends SanguineEventSender {

  /**
   * begins showing the image to be rendered by the view.
   */
  void showImage();

  /**
   * visualizes clicking the given card in the players hand.
   *
   * @param card the card represented by the clicked panel
   * @throws IllegalArgumentException if the given card is not in the player's hand
   */
  void clickCard(Card card);

  /**
   * visualizes clicking the given tile on the game board.
   *
   * @param row the row of the tile (0-indexed from the top)
   * @param col the column of the tile (0-indexed from the left)
   */
  void clickTile(int row, int col);

  /**
   * refreshes the view to be up to date with the model.
   */
  void refresh();

  /**
   * returns whether the view has a clicked card.
   *
   * @return whether the view has a clicked card
   */
  boolean hasClickedCard();

  /**
   * returns the card that is clicked in the view.
   *
   * @return the clicked card
   * @throws IllegalStateException if there is no clicked card
   */
  Card clickedCard();

  /**
   * returns whether there is a clicked tile in the view.
   *
   * @return whether there is a clicked tile in the view
   */
  boolean hasClickedTile();

  /**
   * returns the coordinates of the clicked tile as an array. the first element (idx 0) is the row
   * and the next one is the column.
   *
   * @return the coordinates of the clicked tile.
   * @throws IllegalStateException is there is no clicked tile
   */
  int[] clickedTile();
}
