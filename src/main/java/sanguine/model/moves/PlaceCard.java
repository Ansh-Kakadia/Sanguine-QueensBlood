package sanguine.model.moves;

import sanguine.model.SanguineModel;

/**
 * represents the action of placing a card in a game of sanguine.Sanguine. Card and coordinates
 * are given to constructor.
 */
public class PlaceCard implements SanguineMove {

  private final int row;
  private final int col;
  private final int indexInHand;

  /**
   * constructs a new {@link PlaceCard} command, representing placing the given card on the tile
   * at the given row and column.
   *
   * @param row  the row of the tile
   * @param col  the column of the tile
   * @param indexInHand the index of the card in the hand of the player
   * @throws IllegalArgumentException if any argument is negative
   */
  public PlaceCard(int row, int col, int indexInHand) throws IllegalArgumentException {
    if (row < 0 || col < 0 || indexInHand < 0) {
      throw new IllegalArgumentException("Cannot have negative coordinates or indices");
    }
    this.row = row;
    this.col = col;
    this.indexInHand = indexInHand;
  }

  @Override
  public void affect(SanguineModel model) {
    if (model == null) {
      throw new IllegalArgumentException("null model");
    }
    try {
      model.placeCard(indexInHand, row, col);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Cannot place card at" + indexInHand + " at " + row + " "
          + col);
    } catch (IllegalStateException e) {
      throw new IllegalArgumentException("Illegal move");
    }
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof PlaceCard placeCard)) {
      return false;
    }
    return this.row == placeCard.row && this.col == placeCard.col
        && this.indexInHand == placeCard.indexInHand;
  }

}
