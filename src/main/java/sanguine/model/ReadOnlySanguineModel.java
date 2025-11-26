package sanguine.model;

import java.util.List;
import java.util.Optional;

/**
 * A read only model for a game of Sanguine. For a mutable version of the model, use the
 * {@link SanguineModel} interface. The game is broken down into a grid of tiles that can have
 * a card or pawns on them. More information can be found in the {@link GameTile} interface
 * documentation.
 */
public interface ReadOnlySanguineModel {

  /**
   * returns the width of this game board in tiles.
   *
   * @return the width of this game board
   */
  int width();

  /**
   * returns the height of this game board in tiles.
   *
   * @return the height of this game board
   */
  int height();

  /**
   * returns the card at the given row and column.
   *
   * @param row the row of the tile (0-indexed from the top)
   * @param col the column of the tile (0-indexed from the left)
   * @return the card at the specified tile
   * @throws IllegalStateException    if there is no card at the given tile
   * @throws IllegalArgumentException if the row or column is invalid.
   */
  Card cardAt(int row, int col) throws IllegalArgumentException, IllegalStateException;

  /**
   * returns whether there is a card at the given tile.
   *
   * @param row the row of the tile (0-indexed from the top)
   * @param col the column of the tile (0-indexed from the left)
   * @return whether there is a card at the specified tile
   * @throws IllegalArgumentException if there isn't a tile at the given coordinates
   */
  boolean isCardAt(int row, int col) throws IllegalArgumentException;

  /**
   * returns the number of pawns on the specified tile.
   *
   * @param row the row of the tile (0-indexed from the top)
   * @param col the column of the tile (0-indexed from the left)
   * @return the number of pawns on the tile
   * @throws IllegalArgumentException if there is no tile at the given coordinates
   * @throws IllegalStateException    if there is a card on the specified tile
   */
  int pawnsAt(int row, int col) throws IllegalArgumentException, IllegalStateException;

  /**
   * Returns the owner of the tile at (row,col).
   *
   * @throws IllegalArgumentException if the coordinates are off the board
   * @throws IllegalStateException    if the tile has no owner (no pawns and no card)
   */
  Player ownerAt(int row, int col) throws IllegalArgumentException, IllegalStateException;

  /**
   * returns whether the game is over.
   *
   * @return whether the game is over
   */
  boolean isGameOver();

  /**
   * returns whether the given tile has an owner. an owner is the player that owns a card or pawn(s)
   * on the tile.
   *
   * @param row the row of the tile (0-indexed from the top)
   * @param col the column of the tile (0-indexed from the left)
   * @return whether the given tile has an owner
   * @throws IllegalArgumentException if the coordinates aren't on the grid
   */
  boolean hasOwner(int row, int col) throws IllegalArgumentException;

  /**
   * returns the player whose turn it is.
   *
   * @return the player whose turn it is
   * @throws IllegalStateException if the game is over
   */
  Player getTurn() throws IllegalStateException;

  /**
   * returns the cards in the given player's hand as a list.
   *
   * @param player the player whose hand is to be returned
   * @return the cards in the blue player's hand
   * @throws IllegalArgumentException if the player is null
   */
  List<Card> getHand(Player player) throws IllegalStateException;


  /**
   * returns an optional containing the {@link Player} with a higher score at a given row,
   * or {@code Optional.empty()} if the scores are even.
   *
   * @param row the row we are inspecting
   * @return the player with the higher score at the given row
   * @throws IllegalArgumentException is the row is invalid.
   */
  Optional<Player> getRowWinner(int row) throws IllegalArgumentException;

  /**
   * returns the score of the winner of the row on the row, or 0 incase of a tie.
   *
   * @param row the row to be inspected
   * @return the score of the winner of the row on the row.
   * @throws IllegalArgumentException if the row is invalid
   */
  int getScoreOfRow(int row) throws IllegalArgumentException;

  /**
   * Returns the score of the given player on the given row.
   *
   * @param player player whose score we want
   * @param row    row index (0 indexed from top)
   * @return that player's score on the row
   * @throws IllegalArgumentException if the row is invalid or player is null
   */
  int getRowScore(Player player, int row) throws IllegalArgumentException;

  /**
   * returns an optional containing the {@link Player} with a higher score,
   * or {@code Optional.empty()} if the scores are even.
   *
   * @return the player with the higher score at the given row
   */
  Optional<Player> getWinning();

  /**
   * returns the score of the winning player, or 0 in case of a tie.
   *
   * @return the score of the winning player.
   */
  int getScore();

  /**
   * Returns whether the given player could legally play the card at the given hand index
   * on the tile at (row, col), according to the current game rules and state.
   *
   * @param player      the player attempting to play
   * @param indexInHand index of the card in that player's hand (0 indexed)
   * @param row         target row (0 indexed from  top)
   * @param col         target column (0 indexed from  left)
   * @return true if this play would be legal, false if illegal
   * @throws IllegalArgumentException if player is null or (row,col) is off the board
   */
  boolean canPlayCard(Player player, int indexInHand, int row, int col);

  /**
   * Returns the maximum hand size for this game.
   * The number of cards in a player's hand cannot exceed this value.
   *
   * @return maximum hand size
   */
  int getMaxHandSize();


}
