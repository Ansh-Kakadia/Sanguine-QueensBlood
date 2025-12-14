package sanguine.model.strategy;

import sanguine.model.Player;
import sanguine.model.ReadOnlySanguineModel;
import sanguine.model.moves.Pass;
import sanguine.model.moves.SanguineMove;

/**
 * a strategy that is constructed with two strategies and tries one then another.
 * when {@link #chooseMove} is called, the move given by the first given strategy is computed, if
 * the move is pass, this strategy returns the move given by the second strategy.
 *
 */
public class CombinationStrategy implements SanguineStrategy {

  private final SanguineStrategy first;
  private final SanguineStrategy second;

  /**
   * constructs a new strategy combining the two given strategies as documented
   * by {@link CombinationStrategy}.
   *
   * @param first primary strategy to be tried
   * @param second the strategy used if {@code first} passes
   */
  public CombinationStrategy(SanguineStrategy first, SanguineStrategy second) {
    if (first == null || second == null) {
      throw new IllegalArgumentException("1 or 2 strategy(s) are null");
    }
    this.first = first;
    this.second = second;
  }

  @Override
  public SanguineMove chooseMove(ReadOnlySanguineModel model, Player player)
      throws IllegalArgumentException {
    SanguineMove firstMove = first.chooseMove(model, player);

    if (firstMove instanceof Pass) {
      return second.chooseMove(model, player);
    }
    return firstMove;
  }
}
