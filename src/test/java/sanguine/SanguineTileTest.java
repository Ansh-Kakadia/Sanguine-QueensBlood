package sanguine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import sanguine.model.Card;
import sanguine.model.InfluenceGridTile;
import sanguine.model.Player;
import sanguine.model.SanguineCard;
import sanguine.model.SanguineTile;

/**
 * a suite of tests for the {@link SanguineTile} class.
 */
public class SanguineTileTest {

  SanguineTile gameTile;
  SanguineCard card;

  private void init() {
    gameTile = new SanguineTile();

    List<List<InfluenceGridTile>> influenceGrid1 = new ArrayList<>();
    for (int i = 0; i < 5; i += 1) {
      ArrayList<InfluenceGridTile> row = new ArrayList<>();
      for (int j = 0; j < 5; j += 1) {
        if (i == 2 && j == 2) {
          row.add(InfluenceGridTile.PLACED);
        } else {
          row.add(InfluenceGridTile.UNAFFECTED);
        }
      }
      influenceGrid1.add(row);
    }
    card = new SanguineCard("Card", 1, 1, influenceGrid1);
  }

  @Test(expected = IllegalStateException.class)
  public void testExceptionIfTileIsEmpty() {
    init();
    gameTile.getCard();
  }

  @Test(expected = IllegalStateException.class)
  public void testExceptionIfTileHasCard() {
    init();
    gameTile.addCard(card, Player.BLUE);
    gameTile.pawns();
  }

  @Test(expected = IllegalStateException.class)
  public void testExceptionAddPawnWithCard() {
    init();
    gameTile.addCard(card, Player.RED);
    gameTile.addPawn(Player.RED);
  }

  @Test
  public void testGetCard() {
    init();
    gameTile.addCard(card, Player.BLUE);
    assertEquals(card, gameTile.getCard());
  }

  @Test
  public void testAddPawnsAndPawns() {
    init();
    assertEquals(0, gameTile.pawns());
    gameTile.addPawn(Player.BLUE);
    assertEquals(1, gameTile.pawns());
    gameTile.addPawn(Player.BLUE);
    assertEquals(2, gameTile.pawns());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testExceptionWithNullCard() {
    init();
    gameTile.addCard(null, Player.BLUE);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testExceptionWithNullOwnerAddCard() {
    init();
    gameTile.addCard(card, null);
  }

  @Test
  public void testPlaceOtherPlayerPawnOverridesPawnsAndPlayer() {
    init();
    gameTile.addPawn(Player.BLUE);
    gameTile.addPawn(Player.BLUE);
    assertEquals(2, gameTile.pawns());
    gameTile.addPawn(Player.RED);
    assertEquals(1, gameTile.pawns());
    assertEquals(Player.RED, gameTile.owner());
    gameTile.addPawn(Player.BLUE);
    assertEquals(1, gameTile.pawns());
    assertEquals(Player.BLUE, gameTile.owner());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullOwnerInAddPawnThrowsException() {
    init();
    gameTile.addPawn(null);
  }

  @Test
  public void testNoOwnerOnConstruction() {
    init();
    assertFalse(gameTile.hasOwner());
  }

  @Test
  public void testAddPawnUpdatesHasOwner() {
    init();
    gameTile.addPawn(Player.BLUE);
    assertTrue(gameTile.hasOwner());
  }

  @Test
  public void testCannotHaveMoreThan3Pawns() {
    init();
    gameTile.addPawn(Player.BLUE);
    gameTile.addPawn(Player.BLUE);
    gameTile.addPawn(Player.BLUE);
    assertEquals(3, gameTile.pawns());
    gameTile.addPawn(Player.BLUE);
    assertEquals(3, gameTile.pawns());
  }

  @Test
  public void testAddCardUpdatesOwner() {
    init();
    gameTile.addCard(card, Player.BLUE);
    assertTrue(gameTile.hasOwner());
  }

  @Test(expected = IllegalStateException.class)
  public void addPawn_throwsWhenCardPresent() {
    SanguineTile t = new SanguineTile();
    t.addCard(new SanguineCard("X", 1, 1,
        java.util.Arrays.asList(
            java.util.Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED),
            java.util.Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED),
            java.util.Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.PLACED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED),
            java.util.Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED),
            java.util.Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED)
        )
    ), Player.RED);
    t.addPawn(Player.RED);
  }


  @Test
  public void addCardWithOwner_setsHasCard_getCard_cardOwner() {
    SanguineTile t = new SanguineTile();
    Card c = new SanguineCard("X", 1, 1,
        java.util.Arrays.asList(
            java.util.Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED),
            java.util.Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED),
            java.util.Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.PLACED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED),
            java.util.Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED),
            java.util.Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED)
        )
    );
    t.addCard(c, Player.RED);
    assertTrue(t.hasCard());
    assertSame(c, t.getCard());
    assertEquals(Player.RED, t.owner());
  }

  @Test
  public void addCard_oneArg_setsOwnerNull_hasCardTrue() {
    SanguineTile t = new SanguineTile();
    t.addPawn(Player.RED);

    assertFalse(t.hasCard());
    assertEquals(Player.RED, t.owner());
    assertEquals(1, t.pawns());

    Card c = new SanguineCard("Y", 1, 1,
        java.util.Arrays.asList(
            java.util.Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED),
            java.util.Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED),
            java.util.Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.PLACED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED),
            java.util.Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED),
            java.util.Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED)
        )
    );

    t.addCard(c, Player.BLUE);
    assertTrue(t.hasCard());
    assertSame(c, t.getCard());
  }

  @Test
  public void consistency_optionalVsFields_twoArgAddKeepsHasCardTrue() {
    SanguineTile t = new SanguineTile();
    Card c = new SanguineCard("Z", 1, 1,
        java.util.Arrays.asList(
            java.util.Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED),
            java.util.Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED),
            java.util.Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.PLACED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED),
            java.util.Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED),
            java.util.Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
                InfluenceGridTile.UNAFFECTED)
        )
    );
    t.addCard(c, Player.BLUE);
    assertTrue("Two-arg addCard must set hasCard()==true", t.hasCard());
  }
}
