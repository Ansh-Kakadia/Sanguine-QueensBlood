package sanguine.model;

/**
 * represents a tile in a game of sanguine.Sanguine. tiles either have a card in them,
 * or have a number of pawns on them.
 */
public interface GameTile {


  /**
   * returns whether there is a card on this tile.
   *
   * @return whether this tile has a card
   */
  boolean hasCard();


  /**
   * returns whether this tile has an owner.
   *
   * @return whether this tile has an owner
   */
  boolean hasOwner();

  /**
   * returns the card on this tile.
   *
   * @return the card on this tile
   * @throws IllegalStateException if there is not a card on this space
   */
  Card getCard() throws IllegalStateException;

  /**
   * returns the number of pawns  on the tile.
   *
   * @return the number of pawns on the tile
   * @throws IllegalStateException if there is a card on the tile
   */
  int pawns() throws IllegalStateException;


  /**
   * Adds one pawn for the given player to this tile. If the other player has pawn(s) on the tile
   * the new pawn replaces them all. Note that there cannot be more than 3 pawns on a tile, meaning
   * if there are three pawns and one is to be added, we do nothing
   *
   * @param player the player
   * @throws IllegalArgumentException if player is null
   * @throws IllegalStateException    if a card is already on this tile
   */
  void addPawn(Player player);


  /**
   * Places a card on this tile and records its owner.
   *
   * @param card     the card to place
   * @param owner the owning player
   * @throws IllegalArgumentException if card or owner is null
   * @throws IllegalStateException    if a card is already on this tile, or if there are not
   *                                  enough pawns on this tile to place the card
   */
  void addCard(Card card, Player owner);


  /**
   * Returns the owner of the card on this tile, or null if no card is present.
   *
   * @return the card owner
   * @throws IllegalStateException if this tile is not owned
   */
  Player owner();



}
