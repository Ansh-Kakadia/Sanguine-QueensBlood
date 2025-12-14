package sanguine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import sanguine.controller.BasicSanguineController;
import sanguine.controller.GameStateListener;
import sanguine.model.Card;
import sanguine.model.InfluenceGridTile;
import sanguine.model.Player;
import sanguine.model.SanguineModel;
import sanguine.model.moves.SanguineMove;
import sanguine.view.SanguineEventListener;
import sanguine.view.SanguineView;

/**
 * Unit tests for sanguine.controller.BasicSanguineController using
 * fake model and view implementations to verify turn handling, move validation,
 * and view/model interactions.
 */
public class BasicSanguineControllerTest {

  //COMMEDNT TREST
  @Test
  public void cardClicked_whenNotPlayersTurn_showsIllegalMoveAndDoesNotClickCard() {
    FakeModel model = new FakeModel();
    FakeView view = new FakeView();

    model.setTurn(Player.BLUE);

    BasicSanguineController controller =
        new BasicSanguineController(model, view, Player.RED);

    Card card = new DummyCard("A");
    controller.cardClicked(card);

    assertTrue(view.alertIllegalMoveCalled);
    assertEquals("Not your turn", view.lastAlertReason);
    assertFalse(view.clickCardCalled);
  }

  @Test
  public void placeCard_withoutSelection_showsIllegalMoveAndDoesNotCallModelPlaceCard() {
    FakeModel model = new FakeModel();
    FakeView view = new FakeView();

    model.setTurn(Player.RED);
    model.setGameOver();
    view.clearSelection();

    BasicSanguineController controller =
        new BasicSanguineController(model, view, Player.RED);

    controller.placeCard();

    assertTrue(view.alertIllegalMoveCalled);
    assertEquals("Cannot Place Card Without Clicked Card and Tile", view.lastAlertReason);
    assertFalse(model.placeCardCalled);
  }

  @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
  @Test
  public void placeCard_withLegalMove_callsModelPlaceCardAndRefresh() {
    FakeModel model = new FakeModel();
    FakeView view = new FakeView();

    model.setTurn(Player.RED);
    model.setGameOver();

    DummyCard c = new DummyCard("A");
    model.setRedHand(List.of(c));
    model.setCanPlayResult();

    view.setSelectedCard(c);
    view.setSelectedTile();

    BasicSanguineController controller =
        new BasicSanguineController(model, view, Player.RED);

    controller.placeCard();

    assertTrue("Model.placeCard should be called", model.placeCardCalled);
    assertEquals(0, model.placeIndex);
    assertEquals(1, model.placeRow);
    assertEquals(2, model.placeCol);

    assertTrue("View should be refreshed", view.refreshCalled);
    assertFalse("No illegal move should be shown", view.alertIllegalMoveCalled);
  }

  @Test
  public void pass_whenNotPlayersTurn_showsIllegalMove() {
    FakeModel model = new FakeModel();
    FakeView view = new FakeView();

    model.setTurn(Player.BLUE);

    BasicSanguineController controller =
        new BasicSanguineController(model, view, Player.RED);

    controller.pass();

    assertTrue(view.alertIllegalMoveCalled);
    assertEquals("Not your turn", view.lastAlertReason);
    assertFalse(model.passCalled);
  }

  @Test
  public void alertTurn_forThisPlayer_refreshesAndPromptsTurn() {
    FakeModel model = new FakeModel();
    FakeView view = new FakeView();

    BasicSanguineController controller =
        new BasicSanguineController(model, view, Player.RED);

    controller.alertTurn(Player.RED);

    assertTrue("View should be refreshed on any alertTurn", view.refreshCalled);
    assertTrue("View should prompt this player on their turn", view.promptTurnCalled);
  }

  @Test
  public void alertTurn_forOtherPlayer_onlyRefreshes() {
    FakeModel model = new FakeModel();
    FakeView view = new FakeView();

    BasicSanguineController controller =
        new BasicSanguineController(model, view, Player.RED);

    controller.alertTurn(Player.BLUE);

    assertTrue("View should still be refreshed on other player's turn", view.refreshCalled);
    assertFalse("Should not prompt this player when it's not their turn", view.promptTurnCalled);
  }

  @Test
  public void gameOver_displaysGameEnd() {
    FakeModel model = new FakeModel();
    FakeView view = new FakeView();

    BasicSanguineController controller =
        new BasicSanguineController(model, view, Player.RED);

    controller.gameOver();
    assertTrue("Game over should trigger end-of-game display", view.displayGameEndCalled);
  }

  private static class DummyCard implements Card {
    private final String name;

    DummyCard(String name) {
      this.name = name;
    }

    @Override
    public String name() {
      return name;
    }

    @Override
    public int cost() {
      return 1;
    }

    @Override
    public int value() {
      return 1;
    }

    @Override
    public InfluenceGridTile tileAt(int row, int col) {
      throw new UnsupportedOperationException("Not used in controller tests");
    }
  }

  /**
   * Fake view used to observe what the controller does.
   */
  private static class FakeView implements SanguineView {

    boolean alertIllegalMoveCalled = false;
    String lastAlertReason = null;

    boolean clickCardCalled = false;
    Card lastClickedCard = null;

    boolean clickTileCalled = false;
    int lastTileRow = -1;
    int lastTileCol = -1;

    boolean refreshCalled = false;
    boolean showImageCalled = false;
    boolean promptTurnCalled = false;
    boolean displayGameEndCalled = false;

    boolean hasClickedCardFlag = false;
    Card clickedCardValue = null;

    boolean hasClickedTileFlag = false;
    int[] clickedTileValue = null;

    void setSelectedCard(Card c) {
      this.clickedCardValue = c;
      this.hasClickedCardFlag = (c != null);
    }

    void setSelectedTile() {
      this.clickedTileValue = new int[] {1, 2};
      this.hasClickedTileFlag = true;
    }

    void clearSelection() {
      this.clickedCardValue = null;
      this.hasClickedCardFlag = false;
      this.clickedTileValue = null;
      this.hasClickedTileFlag = false;
    }

    @Override
    public void register(SanguineEventListener listener) {
    }

    @Override
    public void showImage() {
      showImageCalled = true;
    }

    @Override
    public void clickCard(Card card) {
      clickCardCalled = true;
      lastClickedCard = card;
    }

    @Override
    public void clickTile(int row, int col) {
      clickTileCalled = true;
      lastTileRow = row;
      lastTileCol = col;
    }

    @Override
    public void refresh() {
      refreshCalled = true;
    }

    @Override
    public boolean hasClickedCard() {
      return hasClickedCardFlag;
    }

    @Override
    public Card clickedCard() {
      if (!hasClickedCardFlag || clickedCardValue == null) {
        throw new IllegalStateException("no clicked card");
      }
      return clickedCardValue;
    }

    @Override
    public boolean hasClickedTile() {
      return hasClickedTileFlag;
    }

    @Override
    public int[] clickedTile() {
      if (!hasClickedTileFlag || clickedTileValue == null) {
        throw new IllegalStateException("no clicked tile");
      }
      return clickedTileValue;
    }

    @Override
    public void promptTurn() {
      promptTurnCalled = true;
    }

    @Override
    public void displayGameEnd() {
      displayGameEndCalled = true;
    }

    @Override
    public void alertIllegalMove(String reason) {
      alertIllegalMoveCalled = true;
      lastAlertReason = reason;
    }
  }

  private static class FakeModel implements SanguineModel {

    Player turn = Player.RED;
    boolean gameOver = false;

    boolean placeCardCalled = false;
    int placeIndex = -1;
    int placeRow = -1;
    int placeCol = -1;

    boolean passCalled = false;

    boolean canPlayResult = false;

    List<Card> redHand = Collections.emptyList();
    List<Card> blueHand = Collections.emptyList();

    int width = 5;
    int height = 3;

    void setTurn(Player p) {
      this.turn = p;
    }

    void setGameOver() {
      this.gameOver = false;
    }

    void setCanPlayResult() {
      this.canPlayResult = true;
    }

    void setRedHand(List<Card> hand) {
      this.redHand = hand;
    }

    void setBlueHand(List<Card> hand) {
      this.blueHand = hand;
    }

    @Override
    public int width() {
      return width;
    }

    @Override
    public int height() {
      return height;
    }

    @Override
    public boolean isGameOver() {
      return gameOver;
    }

    @Override
    public Player getTurn() {
      return turn;
    }

    @Override
    public List<Card> getHand(Player player) {
      return player == Player.RED ? redHand : blueHand;
    }

    @Override
    public boolean canPlayCard(Player player, int indexInHand, int row, int col) {
      return canPlayResult;
    }

    @Override
    public void placeCard(int indexInHand, int row, int col) {
      placeCardCalled = true;
      placeIndex = indexInHand;
      placeRow = row;
      placeCol = col;
    }

    @Override
    public void pass() {
      passCalled = true;
    }

    @Override
    public void register(GameStateListener listener) {
    }

    @Override
    public Card cardAt(int row, int col) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCardAt(int row, int col) {
      throw new UnsupportedOperationException();
    }

    @Override
    public int pawnsAt(int row, int col) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Player ownerAt(int row, int col) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Player> getRowWinner(int row) {
      throw new UnsupportedOperationException();
    }

    @Override
    public int getScoreOfRow(int row) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Player> getWinning() {
      throw new UnsupportedOperationException();
    }

    @Override
    public int getScore() {
      throw new UnsupportedOperationException();
    }

    @Override
    public int getRowScore(Player player, int row) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasOwner(int row, int col) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void makeTurn(SanguineMove move) {
      throw new UnsupportedOperationException();
    }

    @Override
    public int getMaxHandSize() {
      throw new UnsupportedOperationException();
    }
  }
}

