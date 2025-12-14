package sanguine;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import sanguine.model.Player;
import sanguine.model.ReadOnlySanguineModel;
import sanguine.model.SanguineModel;
import sanguine.model.moves.Pass;
import sanguine.model.moves.SanguineMove;
import sanguine.model.strategy.CombinationStrategy;
import sanguine.model.strategy.SanguineStrategy;

/**
 * Tests for CombinationStrategy: it should delegate to the first strategy
 * unless that strategy returns a Pass, in which case it falls back to the second.
 */
public class CombinationStrategyTest {


  private static class StubMove implements SanguineMove {
    @Override
    public void affect(SanguineModel model) {
    }
  }

  private static class StubStrategy implements SanguineStrategy {
    private final SanguineMove toReturn;
    int calls = 0;

    StubStrategy(SanguineMove toReturn) {
      this.toReturn = toReturn;
    }

    @Override
    public SanguineMove chooseMove(ReadOnlySanguineModel model, Player player) {
      calls++;
      return toReturn;
    }
  }

  @Test
  public void firstRealMove_secondPass_returnsFirstMove() {
    SanguineMove realMove = new StubMove();
    StubStrategy first = new StubStrategy(realMove);
    StubStrategy second = new StubStrategy(new Pass());

    CombinationStrategy combo = new CombinationStrategy(first, second);
    SanguineMove chosen = combo.chooseMove(null, Player.RED);

    assertSame(realMove, chosen);
    assertTrue(first.calls >= 1);
  }

  @Test
  public void firstPass_secondRealMove_returnsSecondMove() {
    SanguineMove realMove = new StubMove();
    StubStrategy first = new StubStrategy(new Pass());
    StubStrategy second = new StubStrategy(realMove);

    CombinationStrategy combo = new CombinationStrategy(first, second);
    SanguineMove chosen = combo.chooseMove(null, Player.RED);

    assertSame(realMove, chosen);
    assertTrue(first.calls >= 1);
    assertTrue(second.calls >= 1);
  }

  @Test
  public void bothPass_resultsInSomePass() {
    StubStrategy first = new StubStrategy(new Pass());
    StubStrategy second = new StubStrategy(new Pass());

    CombinationStrategy combo = new CombinationStrategy(first, second);
    SanguineMove chosen = combo.chooseMove(null, Player.RED);

    assertTrue(chosen instanceof Pass);
  }
}
