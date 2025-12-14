package sanguine;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import sanguine.model.Card;
import sanguine.model.InfluenceGridTile;
import sanguine.model.Player;
import sanguine.model.moves.Pass;
import sanguine.model.moves.PlaceCard;
import sanguine.model.moves.SanguineMove;
import sanguine.model.strategy.MaxRowScoreStrategy;
import sanguine.model.strategy.MockReadOnlySanguineModel;

/**
 * Tests for the MaxRowScoreStrategy.
 */
public class MaxRowScoreStrategyTest {

  private static class DummyCard implements Card {

    private final String name;
    private final int value;

    DummyCard(String name, int value) {
      this.name = name;
      this.value = value;
    }

    @Override
    public String name() {
      return this.name;
    }

    @Override
    public int cost() {
      return 1;
    }

    @Override
    public int value() {
      return this.value;
    }

    @Override
    public InfluenceGridTile tileAt(int row, int col) {
      throw new UnsupportedOperationException("tileAt not used in strategy tests");
    }
  }

  @SuppressWarnings("checkstyle:WhitespaceAfter")
  @Test
  public void picksMoveThatFlipsRowInMyFavor_on3x5Board() {
    MockReadOnlySanguineModel mock = new MockReadOnlySanguineModel(5, 3);

    mock.setHand(Player.RED, List.of(
        new DummyCard("A", 2),
        new DummyCard("B", 3)));

    mock.setRowScore(Player.RED, 0, 2);
    mock.setRowScore(Player.BLUE, 0, 4);

    mock.setRowScore(Player.RED, 1, 5);
    mock.setRowScore(Player.BLUE, 1, 3);

    mock.setRowScore(Player.RED, 2, 0);
    mock.setRowScore(Player.BLUE, 2, 0);

    mock.addLegalMove(Player.RED, 1, 0, 2);

    MaxRowScoreStrategy strat = new MaxRowScoreStrategy();
    SanguineMove move = strat.chooseMove(mock, Player.RED);

    Assert.assertTrue(move instanceof PlaceCard);

    String log = mock.getLog();

    String expected =
        """
        isGameOver
        getHand RED
        getRowScore RED row=0
        getRowScore BLUE row=0
        canPlayCard RED idx=0 row=0 col=0
        canPlayCard RED idx=0 row=0 col=1
        canPlayCard RED idx=0 row=0 col=2
        canPlayCard RED idx=0 row=0 col=3
        canPlayCard RED idx=0 row=0 col=4
        canPlayCard RED idx=1 row=0 col=0
        canPlayCard RED idx=1 row=0 col=1
        canPlayCard RED idx=1 row=0 col=2
        """;

    Assert.assertEquals(expected, log);
  }

  @SuppressWarnings("checkstyle:WhitespaceAfter")
  @Test
  public void passesWhenNoRowCanBeFlipped() {
    MockReadOnlySanguineModel mock = new MockReadOnlySanguineModel(5, 3);

    mock.setHand(Player.RED, List.of(new DummyCard("A", 1)));

    for (int row = 0; row < 3; row++) {
      mock.setRowScore(Player.RED, row, 0);
      mock.setRowScore(Player.BLUE, row, 5);
      for (int col = 0; col < 5; col++) {
        mock.addLegalMove(Player.RED, 0, row, col);
      }
    }

    MaxRowScoreStrategy strat = new MaxRowScoreStrategy();
    SanguineMove move = strat.chooseMove(mock, Player.RED);

    Assert.assertTrue(move instanceof Pass);

    String log = mock.getLog();

    int idxScoreRed0 = log.indexOf("getRowScore RED row=0");
    int idxScoreBlue0 = log.indexOf("getRowScore BLUE row=0");
    int idxCanPlay = log.indexOf("canPlayCard RED idx=0 row=0 col=0");

    Assert.assertTrue(idxScoreRed0 >= 0);
    Assert.assertTrue(idxScoreBlue0 > idxScoreRed0);
    Assert.assertTrue(idxCanPlay > idxScoreBlue0);
  }
}
