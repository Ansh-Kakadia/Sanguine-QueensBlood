package sanguine.controller;

import sanguine.model.GameStateAlerter;
import sanguine.model.Player;

/**
 * behaviors to receive alerts when it's a player's turn. implementers must be registered to a
 * {@link GameStateAlerter} object to receive alerts.
 */
public interface GameStateListener {

  /**
   * behavior for when the turn of a game of Sanguine switches to the given {@code player}'s turn.
   *
   * @param player the player whose turn it is
   */
  void alertTurn(Player player);

  /**
   * behavior for when the game of Sanguine is over.
   */
  void gameOver();

  /**
   * behavior for when the game of Sanguine should start.
   */
  void startGame();

}
