package sanguine;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import sanguine.model.Card;
import sanguine.model.CardFileReader;
import sanguine.model.InfluenceGridTile;
import sanguine.model.SanguineCard;

/**
 * a suite of tests for the class {@link CardFileReader}. as a result of testing the outputs of
 * {@link CardFileReader}, we are testing the observers of the {@link SanguineCard} class.
 */
public class CardFileReaderTests {


  private List<Card> cards;

  private void init() {
    cards = CardFileReader.read("./docs/example.deck");
  }

  @Test
  public void testRightNumberOfCards() {
    init();
    assertEquals(15, cards.size());
  }

  @Test
  public void testCorrectCardNames() {
    init();
    assertEquals("Security", cards.get(0).name());
    assertEquals("Queen", cards.get(2).name());
    assertEquals("Crab", cards.get(3).name());
  }

  @Test
  public void testCorrectCardCosts() {
    init();
    assertEquals(1, cards.get(0).cost());
    assertEquals(2, cards.get(9).cost());
    assertEquals(3, cards.get(13).cost());
    assertEquals(3, cards.get(14).cost());

  }

  @Test
  public void testCorrectCardValues() {
    init();
    assertEquals(1, cards.get(0).value());
    assertEquals(2, cards.get(11).value());
    assertEquals(3, cards.get(6).value());
  }

  @Test
  public void testCorrectCardInfluenceGrid() {
    init();

    List<List<InfluenceGridTile>> grid1 = new ArrayList<>();

    grid1.add(
        new ArrayList<>(Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
            InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
            InfluenceGridTile.UNAFFECTED)));

    grid1.add(
        new ArrayList<>(Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.AFFECTED,
            InfluenceGridTile.AFFECTED, InfluenceGridTile.UNAFFECTED,
            InfluenceGridTile.UNAFFECTED)));

    grid1.add(
        new ArrayList<>(Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.AFFECTED,
            InfluenceGridTile.PLACED, InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED)));

    grid1.add(
        new ArrayList<>(Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.AFFECTED,
            InfluenceGridTile.AFFECTED, InfluenceGridTile.UNAFFECTED,
            InfluenceGridTile.UNAFFECTED)));

    grid1.add(
        new ArrayList<>(Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
            InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
            InfluenceGridTile.UNAFFECTED)));

    for (int row = 0; row < 5; row++) {
      for (int col = 0; col < 5; col++) {
        assertEquals(grid1.get(row).get(col), cards.get(6).tileAt(row, col));
      }
    }
  }

}
