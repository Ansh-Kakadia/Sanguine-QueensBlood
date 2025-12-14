package sanguine;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.Test;
import sanguine.controller.GameStateListener;
import sanguine.controller.machineplayers.StrategyPlayer;
import sanguine.model.Card;
import sanguine.model.Player;
import sanguine.model.ReadOnlySanguineModel;
import sanguine.model.SanguineModel;
import sanguine.model.moves.SanguineMove;
import sanguine.model.strategy.SanguineStrategy;

/**
 * Tests the StrategyPlayer, on its own turn it should ask the strategy
 * for a move and then apply that move to the model, on the other player's
 * turn it should do nothing.
 */
public class StrategyPlayerTest {

  private static class FakeMove implements SanguineMove {
    boolean affectCalled = false;

    @Override
    public void affect(SanguineModel model) {
      this.affectCalled = true;
    }
  }

  private static class FakeStrategy implements SanguineStrategy {
    boolean chooseCalled = false;
    final SanguineMove moveToReturn;

    FakeStrategy(SanguineMove moveToReturn) {
      this.moveToReturn = moveToReturn;
    }

    @Override
    public SanguineMove chooseMove(ReadOnlySanguineModel model, Player player) {
      this.chooseCalled = true;
      return moveToReturn;
    }
  }

  private static class FakeModel implements SanguineModel {

    @Override
    public void placeCard(int indexInHand, int row, int col) {
      throw new UnsupportedOperationException("placeCard not used in StrategyPlayerTest");
    }

    @Override
    public boolean hasOwner(int row, int col) {
      return false;
    }

    @Override
    public void pass() {
    }

    @Override
    public int width() {
      return 0;
    }

    @Override
    public int height() {
      return 0;
    }

    @Override
    public Card cardAt(int row, int col) {
      throw new UnsupportedOperationException("cardAt not used in StrategyPlayerTest");
    }

    @Override
    public boolean isCardAt(int row, int col) {
      return false;
    }

    @Override
    public int pawnsAt(int row, int col) {
      return 0;
    }

    @Override
    public Player ownerAt(int row, int col) {
      return Player.RED;
    }

    @Override
    public boolean isGameOver() {
      return false;
    }

    @Override
    public void makeTurn(SanguineMove move) {
      if (move != null) {
        move.affect(this);
      }
    }

    @Override
    public Player getTurn() {
      return Player.RED;
    }

    @Override
    public List<Card> getHand(Player player) {
      return List.of();
    }

    @Override
    public Optional<Player> getRowWinner(int row) {
      return Optional.empty();
    }

    @Override
    public int getScoreOfRow(int row) {
      return 0;
    }

    @Override
    public Optional<Player> getWinning() {
      return Optional.empty();
    }

    @Override
    public int getScore() {
      return 0;
    }

    @Override
    public int getRowScore(Player player, int row) {
      return 0;
    }

    @Override
    public boolean canPlayCard(Player player, int indexInHand, int row, int col) {
      return false;
    }

    @Override
    public int getMaxHandSize() {
      return 0;
    }

    @Override
    public void register(GameStateListener listener) {
    }
  }

  @Test
  public void alertTurn_forThisPlayer_callsStrategyAndAppliesMove() {
    FakeModel model = new FakeModel();
    FakeMove move = new FakeMove();
    FakeStrategy strategy = new FakeStrategy(move);

    StrategyPlayer sp = new StrategyPlayer(model, Player.RED, strategy);

    sp.alertTurn(Player.RED);

    assertTrue("Strategy should be consulted on its own turn", strategy.chooseCalled);
    assertTrue("Returned move should be applied to the model", move.affectCalled);
  }

  @Test
  public void alertTurn_forOtherPlayer_doesNothing() {
    FakeModel model = new FakeModel();
    FakeMove move = new FakeMove();
    FakeStrategy strategy = new FakeStrategy(move);

    StrategyPlayer sp = new StrategyPlayer(model, Player.RED, strategy);

    sp.alertTurn(Player.BLUE);

    assertFalse("Strategy should NOT be consulted on other player's turn", strategy.chooseCalled);
    assertFalse("Move should NOT be applied on other player's turn", move.affectCalled);
  }

  @Test
  public void gameOver_doesNothing() {
    FakeModel model = new FakeModel();
    FakeMove move = new FakeMove();
    FakeStrategy strategy = new FakeStrategy(move);

    StrategyPlayer sp = new StrategyPlayer(model, Player.RED, strategy);

    sp.gameOver();

    assertFalse("gameOver should not trigger strategy", strategy.chooseCalled);
    assertFalse("gameOver should not apply any move", move.affectCalled);
  }
}
