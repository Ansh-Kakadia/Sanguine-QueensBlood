package sanguine.model.strategy;

import sanguine.model.Player;
import sanguine.model.ReadOnlySanguineModel;
import sanguine.model.moves.SanguineMove;

/**
 * A strategy for choosing a move in a game of Sanguine.
 * Implementations take a read-only view of the game state and the player
 * they are playing as, and return a SanguineMove to make.
 */
public interface SanguineStrategy {

  /**
   * Choose a move for the given player in the given game state.
   *
   * @param model  the current game state (readonly)
   * @param player player this strategy is making a move for
   * @return a move to make
   * @throws IllegalArgumentException if model or player is null
   */
  SanguineMove chooseMove(ReadOnlySanguineModel model, Player player)
      throws IllegalArgumentException;
}