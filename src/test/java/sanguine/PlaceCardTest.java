package sanguine;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import sanguine.model.Card;
import sanguine.model.InfluenceGridTile;
import sanguine.model.Player;
import sanguine.model.SanguineCard;
import sanguine.model.SanguineModel;
import sanguine.model.moves.PlaceCard;
import sanguine.model.moves.SanguineMove;

/**
 * Tests the command wrapper PlaceCard: it should call SanguineModel.placeCard(...)
 * and map exceptions to IllegalArgumentException as specified.
 */
public class PlaceCardTest {

  private static Card trivialCard() {
    List<List<InfluenceGridTile>> grid = Arrays.asList(
        Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
            InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
            InfluenceGridTile.UNAFFECTED),
        Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
            InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
            InfluenceGridTile.UNAFFECTED),
        Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
            InfluenceGridTile.PLACED, InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED),
        Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
            InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
            InfluenceGridTile.UNAFFECTED),
        Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
            InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
            InfluenceGridTile.UNAFFECTED)
    );
    return new SanguineCard("T", 1, 1, grid);
  }


  @Test(expected = IllegalArgumentException.class)
  public void affect_nullModel_throwsException() {
    Card c = trivialCard();
    PlaceCard cmd = new PlaceCard(0, 0, 0);
    cmd.affect(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void affect_modelThrowsException_isPropagatedAsException() {
    FakeModel m = new FakeModel();
    m.placeCardThrows = new IllegalArgumentException("bad");
    Card c = trivialCard();
    new PlaceCard(0, 0, 0).affect(m);
  }

  @Test(expected = IllegalArgumentException.class)
  public void affectModelThrowsExceptionWrappedAsIllegalArgumentException() {
    FakeModel m = new FakeModel();
    m.placeCardThrows = new IllegalStateException("bad");
    Card c = trivialCard();
    new PlaceCard(0, 0, 0).affect(m);
  }



  private static class FakeModel implements SanguineModel {
    int lastIndex;
    Integer lastRow;
    Integer lastCol;

    RuntimeException placeCardThrows;

    boolean passCalled = false;

    @Override
    public void placeCard(int indexInHand, int row, int col) {
      if (placeCardThrows != null) {
        throw placeCardThrows;
      }
      this.lastIndex = indexInHand;
      this.lastRow = row;
      this.lastCol = col;
    }

    @Override
    public boolean hasOwner(int row, int col) throws IllegalArgumentException {
      return false;
    }

    @Override
    public void pass() {
      passCalled = true;
    }

    @Override
    public int width() {
      return 5;
    }

    @Override
    public int height() {
      return 3;
    }

    @Override
    public Card cardAt(int row, int col) {
      throw new UnsupportedOperationException();
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
    public Player ownerAt(int row, int col) throws IllegalArgumentException, IllegalStateException {
      return Player.RED;
    }

    @Override
    public boolean isGameOver() {
      return false;
    }

    @Override
    public void makeTurn(SanguineMove move) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Player getTurn() {
      return Player.RED;
    }

    @Override
    public List<Card> getHand(Player player) throws IllegalStateException {
      return List.of();
    }

    @Override
    public Optional<Player> getRowWinner(int row) throws IllegalArgumentException {
      return Optional.empty();
    }

    @Override
    public int getScoreOfRow(int row) throws IllegalArgumentException {
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
    public int getRowScore(Player player, int row) throws IllegalArgumentException {
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
  }
}
