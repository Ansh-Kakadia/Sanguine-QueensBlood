package sanguine.model;


import sanguine.model.moves.SanguineMove;

/**
 * the behaviors needed to play a game of sanguine.Sanguine. To make a turn, a player can either
 * pass with {@code pass}, or can place a card with {@code placeCard}, or can make a move
 * implementing {@link SanguineMove} with the {@code makeMove} method. Coordinates are represented
 * as (row,col), being 0-indexed from the top and left respectively.
 */
public interface SanguineModel extends ReadOnlySanguineModel, GameStateAlerter {

  /**
   * passes the current turn.
   *
   * @throws IllegalStateException if the game is over
   */
  void pass() throws IllegalStateException;

  /**
   * places the given card on the specified tile.
   *
   * @param indexInHand the index of the card in the hand of the player whose turn it is
   *                    (0-indexed), index is ties to  {@code getHand(Player)}
   * @param row         the row of the tile (0-indexed from the top)
   * @param col         the column of the tile (0-indexed from the left)
   * @throws IllegalArgumentException if the coordinates are invalid, or if there isn't a card at
   *                                  this index in the hand
   * @throws IllegalStateException    if the move is illegal, or if the game is over
   */
  void placeCard(int indexInHand, int row, int col)
      throws IllegalArgumentException, IllegalStateException;

  /**
   * makes the given move on this model.
   *
   * @param move the move to be made on this model.
   * @throws IllegalArgumentException if the move is null
   * @throws IllegalStateException    if the move is illegal
   */
  void makeTurn(SanguineMove move) throws IllegalArgumentException, IllegalStateException;

  /**
   * Starts the game and triggers any initial model status notifications.
   * This should be called once after all views/ controllers have registered as listeners.
   * Default implementation does nothing, but concrete models may override.
   *
   * @throws IllegalStateException if the game has already been started or cannot be started
   */
  default void startGame() throws IllegalStateException{

  }
}
