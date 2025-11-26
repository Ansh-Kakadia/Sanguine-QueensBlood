package sanguine.model;

/**
 * represents a card in a game of sanguine. each card has a name, cost, value, and influence grid.
 * the influence grid size is 5x5. Cards should be immutable.
 */
public interface Card {

  /**
   * returns the name of the card.
   *
   * @return the name of the card
   */
  String name();

  /**
   * returns the cost of the card in pawns.
   *
   * @return the cost of the card
   */
  int cost();

  /**
   * returns the value of the card.
   *
   * @return the value of the card
   */
  int value();

  /**
   * returns the type of tile at the given coordinates in the influence grid.
   *
   * @param row the row of the tile (0-indexed from the top)
   * @param col the column of the tile (0-indexed from the left)
   * @return the type of tile at the given coordinates in the influence grid
   * @throws IllegalArgumentException if given invalid coordinates
   */
  InfluenceGridTile tileAt(int row, int col) throws IllegalArgumentException;


}
