package sanguine.model;

/**
 * represents a tile on an influence grid in a game of sanguine.Sanguine.
 * {@code AFFECTED} tiles are influenced when the card is placed.
 * {@code UNAFFECTED} tiles aren't influenced when the card is placed.
 * {@code PLACED} tiles represent the tile the card is placed on.
 */
public enum InfluenceGridTile {

  AFFECTED,
  UNAFFECTED,
  PLACED;

  @Override
  public String toString() {
    return switch (this) {
      case AFFECTED -> "I";
      case UNAFFECTED -> "X";
      case PLACED -> "C";
    };
  }
}
