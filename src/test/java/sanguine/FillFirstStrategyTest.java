package sanguine;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import sanguine.model.Card;
import sanguine.model.InfluenceGridTile;
import sanguine.model.Player;
import sanguine.model.ReadOnlySanguineModel;
import sanguine.model.moves.Pass;
import sanguine.model.moves.PlaceCard;
import sanguine.model.moves.SanguineMove;
import sanguine.model.strategy.FillFirstStrategy;
import sanguine.model.strategy.MockReadOnlySanguineModel;

/**
 * Tests for the FillFirstStrategy.
 */
public class FillFirstStrategyTest {

  private static class MockModelForFillFirst implements ReadOnlySanguineModel {

    private final int width;
    private final int height;
    private final List<Card> hand;
    private final Player expectedPlayer;
    private final int trueHandIndex;
    private final int trueRow;
    private final int trueCol;
    private final StringBuilder log;

    MockModelForFillFirst(int width,
                          int height,
                          int handSize,
                          Player expectedPlayer,
                          int trueHandIndex,
                          int trueRow,
                          int trueCol) {
      this.width = width;
      this.height = height;
      this.expectedPlayer = expectedPlayer;
      this.trueHandIndex = trueHandIndex;
      this.trueRow = trueRow;
      this.trueCol = trueCol;
      this.log = new StringBuilder();

      this.hand = new ArrayList<>();
      for (int i = 0; i < handSize; i++) {
        this.hand.add(new DummyCard("C" + i, 1));
      }
    }

    String getLog() {
      return this.log.toString();
    }

    @Override
    public int width() {
      return this.width;
    }

    @Override
    public int height() {
      return this.height;
    }

    @Override
    public boolean isGameOver() {
      return false;
    }

    @Override
    public List<Card> getHand(Player player) {
      if (player != this.expectedPlayer) {
        throw new IllegalArgumentException("Unexpected player: " + player);
      }
      return new ArrayList<Card>(this.hand);
    }

    @Override
    public boolean canPlayCard(Player player, int indexInHand, int row, int col) {
      this.log.append("canPlayCard ")
          .append(player)
          .append(" index=")
          .append(indexInHand)
          .append(" row=")
          .append(row)
          .append(" col=")
          .append(col)
          .append("\n");

      if (player != this.expectedPlayer) {
        return false;
      }

      if (this.trueHandIndex < 0 || this.trueRow < 0 || this.trueCol < 0) {
        return false;
      }

      return indexInHand == this.trueHandIndex
          && row == this.trueRow
          && col == this.trueCol;
    }

    @Override
    public Card cardAt(int row, int col) {
      throw new UnsupportedOperationException("cardAt not used in this test");
    }

    @Override
    public boolean isCardAt(int row, int col) {
      throw new UnsupportedOperationException("isCardAt not used in this test");
    }

    @Override
    public int pawnsAt(int row, int col) {
      throw new UnsupportedOperationException("pawnsAt not used in this test");
    }

    @Override
    public Player ownerAt(int row, int col) {
      throw new UnsupportedOperationException("ownerAt not used in this test");
    }

    @Override
    public boolean hasOwner(int row, int col) {
      throw new UnsupportedOperationException("hasOwner not used in this test");
    }

    @Override
    public Player getTurn() {
      throw new UnsupportedOperationException("getTurn not used in this test");
    }

    @Override
    public Optional<Player> getRowWinner(int row) {
      throw new UnsupportedOperationException("getRowWinner not used in this test");
    }

    @Override
    public int getScoreOfRow(int row) {
      throw new UnsupportedOperationException("getScoreOfRow not used in this test");
    }

    @Override
    public int getRowScore(Player player, int row) {
      throw new UnsupportedOperationException("getRowScore not used in this test");
    }

    @Override
    public Optional<Player> getWinning() {
      throw new UnsupportedOperationException("getWinning not used in this test");
    }

    @Override
    public int getScore() {
      throw new UnsupportedOperationException("getScore not used in this test");
    }

    @Override
    public int getMaxHandSize() {
      return this.hand.size();
    }
  }

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
      throw new UnsupportedOperationException("tileAt not used in this test");
    }
  }

  @Test
  public void testFillFirstStrategyPicksFirstLegalMoveInRowMajorOrder() {
    MockModelForFillFirst mock = new MockModelForFillFirst(
        3,
        2,
        2,
        Player.RED,
        1,
        1,
        2
    );

    FillFirstStrategy strategy = new FillFirstStrategy();
    SanguineMove move = strategy.chooseMove(mock, Player.RED);

    Assert.assertTrue(move instanceof PlaceCard);

    String expectedLog =
        """
            canPlayCard RED index=0 row=0 col=0
            canPlayCard RED index=0 row=0 col=1
            canPlayCard RED index=0 row=0 col=2
            canPlayCard RED index=0 row=1 col=0
            canPlayCard RED index=0 row=1 col=1
            canPlayCard RED index=0 row=1 col=2
            canPlayCard RED index=1 row=0 col=0
            canPlayCard RED index=1 row=0 col=1
            canPlayCard RED index=1 row=0 col=2
            canPlayCard RED index=1 row=1 col=0
            canPlayCard RED index=1 row=1 col=1
            canPlayCard RED index=1 row=1 col=2
            """;

    Assert.assertEquals(expectedLog, mock.getLog());
  }

  @Test
  public void testFillFirstStrategyPassesWhenNoLegalMove() {
    MockModelForFillFirst mock = new MockModelForFillFirst(
        3,
        2,
        2,
        Player.RED,
        -1,
        -1,
        -1
    );

    FillFirstStrategy strategy = new FillFirstStrategy();
    SanguineMove move = strategy.chooseMove(mock, Player.RED);

    Assert.assertTrue(move instanceof Pass);
  }

  @SuppressWarnings("checkstyle:WhitespaceAfter")
  @Test
  public void picksFirstLegalMove_rowMajorOrder() {
    MockReadOnlySanguineModel mock = new MockReadOnlySanguineModel(3, 2);

    mock.setHand(Player.RED, List.of(new DummyCard("A", 1), new DummyCard("B",
        1)));
    mock.addLegalMove(Player.RED, 1, 1, 2);

    FillFirstStrategy strat = new FillFirstStrategy();
    SanguineMove move = strat.chooseMove(mock, Player.RED);

    Assert.assertTrue(move instanceof PlaceCard);

    String expected =
        """
        isGameOver
        getHand RED
        canPlayCard RED idx=0 row=0 col=0
        canPlayCard RED idx=0 row=0 col=1
        canPlayCard RED idx=0 row=0 col=2
        canPlayCard RED idx=0 row=1 col=0
        canPlayCard RED idx=0 row=1 col=1
        canPlayCard RED idx=0 row=1 col=2
        canPlayCard RED idx=1 row=0 col=0
        canPlayCard RED idx=1 row=0 col=1
        canPlayCard RED idx=1 row=0 col=2
        canPlayCard RED idx=1 row=1 col=0
        canPlayCard RED idx=1 row=1 col=1
        canPlayCard RED idx=1 row=1 col=2
        """;

    Assert.assertEquals(expected, mock.getLog());
  }

  @SuppressWarnings("checkstyle:WhitespaceAfter")
  @Test
  public void passesWhenNoLegalMove() {
    MockReadOnlySanguineModel mock = new MockReadOnlySanguineModel(3, 2);
    mock.setHand(Player.RED, List.of(new DummyCard("A",1), new DummyCard("B",
        1)));

    FillFirstStrategy strat = new FillFirstStrategy();
    SanguineMove move = strat.chooseMove(mock, Player.RED);

    Assert.assertTrue(move instanceof Pass);
  }
}
