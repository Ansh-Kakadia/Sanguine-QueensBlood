package sanguine.controller.machineplayers;

import sanguine.controller.GameStateListener;
import sanguine.model.Player;
import sanguine.model.SanguineModel;
import sanguine.model.strategy.SanguineStrategy;

/**
 * A player controlled by a machine that listens for turn notifications and
 * on its own turn asks a {@link sanguine.model.strategy.SanguineStrategy}
 * for a {@link sanguine.model.moves.SanguineMove} and applies it to the
 * underlying {@link sanguine.model.SanguineModel}.
 */
public class StrategyPlayer implements GameStateListener {

  private final SanguineModel model;
  private final Player player;
  private final SanguineStrategy strategy;

  /**
   * constructs a strategy player that plays the given strategy's move when it's the given player's
   * turn.
   *
   * @param model the model the moves are made on
   * @param player the player the moves are made for
   * @param strategy the strategy that picks the moves
   * @throws IllegalArgumentException if any arguments are null
   */
  public StrategyPlayer(SanguineModel model, Player player, SanguineStrategy strategy) {
    if (model == null || player == null || strategy == null) {
      throw new IllegalArgumentException("model or player is null");
    }

    this.model = model;
    this.player = player;
    this.strategy = strategy;
    model.register(this);
  }

  @Override
  public void alertTurn(Player player) {
    if (player == this.player) {
      strategy.chooseMove(model, player).affect(model);
    }
    // ADD BEHAVIOR IF NEEDED
  }

  @Override
  public void gameOver() {
    // do nothing
  }

  @Override
  public void startGame() {
    // do nothing
  }
}
