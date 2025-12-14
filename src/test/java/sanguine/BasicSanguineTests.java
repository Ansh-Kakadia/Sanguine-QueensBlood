package sanguine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Optional;
import org.junit.Test;
import sanguine.controller.GameStateListener;
import sanguine.model.BasicSanguine;
import sanguine.model.Card;
import sanguine.model.InfluenceGridTile;
import sanguine.model.Player;
import sanguine.model.SanguineCard;
import sanguine.model.SanguineModel;

/**
 * a suite of tests for the {@link BasicSanguine} class.
 */
public class BasicSanguineTests {

  @Test(expected = IllegalArgumentException.class)
  public void testSanguineNegativeWidthThrowsException() {
    new BasicSanguine(-1, 3, 1, "./docs/example.deck", "./docs/example.deck");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSanguineNegativeHeightThrowsException() {
    new BasicSanguine(3, -3, 1, "./docs/example.deck", "./docs/example.deck");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSanguineOneWidthThrowsException() {
    new BasicSanguine(1, 10, 1, "./docs/example.deck", "./docs/example.deck");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSanguineEvenWidthThrowsException() {
    new BasicSanguine(6, 3, 1, "./docs/example.deck", "./docs/example.deck");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSanguineZeroHeightThrowsException() {
    new BasicSanguine(5, 0, 1, "./docs/example.deck", "./docs/example.deck");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNotEnoughCardsThrowsException() {
    new BasicSanguine(7, 5, 1, "./docs/example.deck", "./docs/example.deck");
  }

  @Test(expected = IllegalArgumentException.class)
  public void test3OfSameCardRedDeckThrowsException() {
    new BasicSanguine(3, 3, 1, "./docs/3identical.deck", "./docs/example.deck");
  }

  @Test(expected = IllegalArgumentException.class)
  public void test3OfSameCardBlueDeckThrowsException() {
    new BasicSanguine(3, 3, 1, "./docs/example.deck", "./docs/3identical.deck");
  }

  // FROM THIS POINT ON, ALL THE TESTS ARE NEW, AND BELOW THERE ARE A FEW PRIVATE HELPER METHDOS
  @Test
  public void placeCard_happyPath_switchesTurn_resetsPass() throws Exception {
    Path deck = writeTempDeckNoInfluence(1);
    SanguineModel g = new BasicSanguine(5, 3, 2, deck.toString(), deck.toString());
    assertEquals(Player.RED, g.getTurn());
    g.placeCard(0, 0, 0);

    assertTrue(g.isCardAt(0, 0));
    assertEquals(Player.BLUE, g.getTurn());
    g.pass();
    assertFalse(g.isGameOver());
  }

  @Test
  public void placeCard_costChargedFromCurrentPlayerOnly_negativeCase() throws Exception {
    Path deck = writeTempDeckPlusCross(10 /*cost=*/);
    SanguineModel g = new BasicSanguine(5, 3, 1, deck.toString(), deck.toString());

    Card plus = new SanguineCard("Plus", 1, 1,
        java.util.Arrays.asList(
            java.util.Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED),
            java.util.Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.AFFECTED,
                InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED),
            java.util.Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.PLACED, InfluenceGridTile.AFFECTED, InfluenceGridTile.UNAFFECTED),
            java.util.Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.AFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED),
            java.util.Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED)
        )
    );
    g.placeCard(0, 1, 0);
    assertEquals(Player.BLUE, g.getTurn());
    g.pass();
    g.pass();

    try {
      g.placeCard(0, 1, 1);
      fail("Expected IllegalStateException due to not enough current-player pawns");
    } catch (IllegalStateException expected) {
    }
  }

  @Test
  public void placeCardThrowsWhenCardPresentOutOfBoundsAfterGameOver() throws Exception {
    Path deck = writeTempDeckNoInfluence(1);
    SanguineModel g = new BasicSanguine(5, 3, 1, deck.toString(), deck.toString());


    g.placeCard(0, 0, 0);
    assertTrue(g.isCardAt(0, 0));

    try {
      g.placeCard(0, 0, 0);
      fail("Expected IllegalStateException: card already at tile");
    } catch (IllegalStateException expected) {
    }
    try {
      g.placeCard(0, -1, 0);
      fail("Expected IllegalArgumentException for OOB");
    } catch (IllegalArgumentException expected) {
    }
    try {
      g.placeCard(0, 0, 999);
      fail("Expected IllegalArgumentException for OOB");
    } catch (IllegalArgumentException expected) {
    }

    g.pass();
    g.pass();
    assertTrue(g.isGameOver());
    try {
      g.placeCard(0, 1, 0);
      fail("Expected IllegalStateException after game over");
    } catch (IllegalStateException expected) {

    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testHighHandSizeThrowsException() {
    new BasicSanguine(3, 3, 6, "./docs/example.deck", "./docs/example.deck");
  }

  @Test
  public void testPassThenPlaceResetsCounter() throws Exception {
    Path deck = writeTempDeckNoInfluence(1);
    SanguineModel g = new BasicSanguine(5, 3, 1, deck.toString(), deck.toString());
    g.pass();

    g.placeCard(0, 0, 4);
    g.pass();
    assertFalse(g.isGameOver());
  }

  @Test
  public void influence_addsPawn_onEmpty() throws Exception {
    Path deck = writeTempDeckPlusCross(8);
    SanguineModel g = new BasicSanguine(5, 3, 1, deck.toString(), deck.toString());


    assertFalse(g.isCardAt(1, 1));

    assertEquals(0, g.pawnsAt(1, 1));


    g.placeCard(0, 1, 0);

    assertFalse(g.isCardAt(1, 1));
    assertEquals("Expected 1 pawn from influence", 1, g.pawnsAt(1, 1));
  }

  @Test
  public void influence_blueMirrorsHorizontally() throws Exception {
    Path deck = writeTempDeckPlusCross(8);
    {
      SanguineModel g = new BasicSanguine(5, 3, 1, deck.toString(), deck.toString());

      g.placeCard(0, 1, 0);
      assertFalse(g.isCardAt(1, 1));
      assertEquals(1, g.pawnsAt(1, 1));
    }

    {
      SanguineModel g = new BasicSanguine(5, 3, 1, deck.toString(), deck.toString());
      g.pass();

      g.placeCard(0, 1, 4);
      assertFalse(g.isCardAt(1, 3));
      assertEquals(1, g.pawnsAt(1, 3));
    }
  }

  @Test
  public void cardAt_isCardAt_pawnsAt_contract() throws Exception {
    Path deck = writeTempDeckNoInfluence(1);
    SanguineModel g = new BasicSanguine(5, 3, 1, writeTempDeckNoInfluence(1).toString(),
        writeTempDeckNoInfluence(2).toString());


    assertFalse(g.isCardAt(0, 1));
    try {
      g.cardAt(0, 1);
      fail("Expected IllegalArgumentException for cardAt on empty");
    } catch (IllegalArgumentException expected) {
    }
    assertTrue(g.pawnsAt(0, 1) >= 0);

    g.placeCard(0, 0, 0);
    assertTrue(g.isCardAt(0, 0));
    try {
      g.pawnsAt(0, 0);
      fail("Expected IllegalStateException for pawnsAt on card tile");
    } catch (IllegalStateException expected) {
    }
  }

  @Test
  public void isGameOver_twoPassesOnly() throws Exception {
    Path deck = writeTempDeckNoInfluence(1);
    SanguineModel g = new BasicSanguine(5, 3, 1, deck.toString(), deck.toString());
    assertFalse(g.isGameOver());
    g.pass();
    assertFalse(g.isGameOver());
    g.pass();
    assertTrue(g.isGameOver());
  }

  @Test
  public void testScoreIncreasesAfterCardPlaced() throws Exception {
    Path deck = writeTempDeckNoInfluence(1);
    SanguineModel g = new BasicSanguine(5, 3, 1, deck.toString(), deck.toString());
    g.placeCard(0, 0, 0);
    assertEquals(Player.RED, g.getRowWinner(0).get());
    assertEquals(Optional.empty(), g.getRowWinner(1));
    assertEquals(0, g.getScoreOfRow(1));
    assertEquals(Player.RED, g.getWinning().get());
    assertEquals(1, g.getScore());
    g.placeCard(0, 0, 4);
    assertEquals(Optional.empty(), g.getRowWinner(0));
    assertEquals(Optional.empty(), g.getWinning());
    assertEquals(0, g.getScore());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetRowWinnerThrowsExceptionWithOutOfBoundsRow() throws Exception {
    Path deck = writeTempDeckPlusCross(8);
    SanguineModel g = new BasicSanguine(5, 3, 1, deck.toString(), deck.toString());
    g.getRowWinner(3);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetRowWinnerThrowsExceptionWithNegativeRow() throws Exception {
    Path deck = writeTempDeckPlusCross(8);
    SanguineModel g = new BasicSanguine(5, 3, 1, deck.toString(), deck.toString());
    g.getRowWinner(-1);
  }

  /**
   * This is to write a temp deck configuration file with N copies of a card with no influence and
   * cost=cost card.
   */
  private Path writeTempDeckNoInfluence(int cost) throws IOException {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 8; i++) {
      sb.append(String.format(Locale.US, "NoInf%d %d %d%n", i, cost, 1));
      sb.append("XXXXX\n");
      sb.append("XXXXX\n");
      sb.append("XXCXX\n");
      sb.append("XXXXX\n");
      sb.append("XXXXX\n");
    }
    Path p = Files.createTempFile("deck-noinf-", ".config");
    Files.write(p, sb.toString().getBytes());
    p.toFile().deleteOnExit();
    return p;
  }

  /**
   * This one writes a temp deck configuarion file with N copies of a card
   * that's plus-cross influence and cost = card.
   */
  private Path writeTempDeckPlusCross(int copies) throws IOException {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < copies; i++) {
      sb.append(String.format(Locale.US, "Plus%d %d %d%n", i, 1, 1));
      sb.append("XXXXX\n");
      sb.append("..I..\n".replace('.', 'X'));
      sb.append("XICIX\n");
      sb.append("..I..\n".replace('.', 'X'));
      sb.append("XXXXX\n");

    }
    Path p = Files.createTempFile("deck-plus-", ".config");
    Files.write(p, sb.toString().getBytes());
    p.toFile().deleteOnExit();
    return p;
  }

  @Test
  public void testStartGame() {
    SanguineModel model = new BasicSanguine(5, 3, 1, "./docs/example.deck", "./docs/example.deck");
    GameStateListenerMock listener = new GameStateListenerMock();
    model.register(listener);
    model.startGame();
    assertTrue(listener.startGame);
    assertTrue(listener.alertedTurn);
  }

  @Test
  public void testGameOver() {
    SanguineModel model = new BasicSanguine(5, 3, 1, "./docs/example.deck", "./docs/example.deck");
    GameStateListenerMock listener = new GameStateListenerMock();
    model.register(listener);
    model.pass();
    model.pass();
    assertTrue(model.isGameOver());
    assertTrue(listener.gameOver);
    assertFalse(listener.startGame);
    assertTrue(listener.alertedTurn);
  }

  private class GameStateListenerMock implements GameStateListener {

    public boolean gameOver = false;
    public boolean startGame = false;
    public boolean alertedTurn = false;

    @Override
    public void alertTurn(Player player) {
      alertedTurn = true;
    }

    @Override
    public void gameOver() {
      gameOver = true;
    }

    @Override
    public void startGame() {
      startGame = true;
    }
  }
}
