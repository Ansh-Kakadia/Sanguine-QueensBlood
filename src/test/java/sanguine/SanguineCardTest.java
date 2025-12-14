package sanguine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import sanguine.model.Card;
import sanguine.model.InfluenceGridTile;
import sanguine.model.SanguineCard;

/**
 * Tests of the {@link Card} class.
 */
public class SanguineCardTest {

  private static List<List<InfluenceGridTile>> plusGrid() {
    return Arrays.asList(
        Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
            InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
            InfluenceGridTile.UNAFFECTED),
        Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.AFFECTED,
            InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
            InfluenceGridTile.UNAFFECTED),
        Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
            InfluenceGridTile.PLACED, InfluenceGridTile.AFFECTED, InfluenceGridTile.UNAFFECTED),
        Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
            InfluenceGridTile.AFFECTED, InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED),
        Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
            InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
            InfluenceGridTile.UNAFFECTED)
    );
  }

  private List<List<InfluenceGridTile>> gridPlus() {
    return Arrays.asList(
        Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
            InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
            InfluenceGridTile.UNAFFECTED),
        Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.AFFECTED,
            InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
            InfluenceGridTile.UNAFFECTED),
        Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
            InfluenceGridTile.PLACED, InfluenceGridTile.AFFECTED, InfluenceGridTile.UNAFFECTED),
        Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
            InfluenceGridTile.AFFECTED, InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED),
        Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
            InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
            InfluenceGridTile.UNAFFECTED)
    );
  }

  @Test
  public void tileAt_returnsCorrectTiles() {
    Card c = new SanguineCard("Plus", 1, 7, gridPlus());
    assertEquals(InfluenceGridTile.PLACED, c.tileAt(2, 2));
    assertEquals(InfluenceGridTile.AFFECTED, c.tileAt(2, 3));
    assertEquals(InfluenceGridTile.AFFECTED, c.tileAt(1, 1));
    assertEquals(InfluenceGridTile.UNAFFECTED, c.tileAt(0, 0));
  }

  @Test
  public void constructorDefensiveCopy_gridMutationDoesNotAffectCard() {
    List<List<InfluenceGridTile>> g = gridPlus();
    List<List<InfluenceGridTile>> outer = new ArrayList<>();
    for (List<InfluenceGridTile> row : g) {
      outer.add(new ArrayList<>(row));
    }
    Card c = new SanguineCard("Plus", 2, 3, outer);
    outer.get(1).set(1, InfluenceGridTile.UNAFFECTED);
    outer.get(2).set(2, InfluenceGridTile.UNAFFECTED);

    assertEquals(InfluenceGridTile.AFFECTED, c.tileAt(1, 1));
    assertEquals(InfluenceGridTile.PLACED, c.tileAt(2, 2));
  }

  @Test
  public void gridDefensiveCopy_mutationDoesNotAffectCard() {
    List<List<InfluenceGridTile>> original = plusGrid();
    List<List<InfluenceGridTile>> mutableOuter = new ArrayList<>();
    for (List<InfluenceGridTile> row : original) {
      mutableOuter.add(new ArrayList<>(row));
    }

    Card c = new SanguineCard("Plus", 2, 7, mutableOuter);

    mutableOuter.get(1).set(1, InfluenceGridTile.UNAFFECTED);
    mutableOuter.get(2).set(2, InfluenceGridTile.UNAFFECTED);

    assertEquals(InfluenceGridTile.AFFECTED, c.tileAt(1, 1));
    assertEquals(InfluenceGridTile.PLACED, c.tileAt(2, 2));
  }

  @Test
  public void equals_hashCode_sameFieldsSameGridAreEqual() {
    Card a = new SanguineCard("Plus", 2, 7, plusGrid());
    Card b = new SanguineCard("Plus", 2, 7, plusGrid());
    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());
  }

  @Test(expected = IllegalArgumentException.class)
  public void tileAt_throwsOnBadCoords_rowLow() {
    Card c = new SanguineCard("Plus", 2, 7, plusGrid());
    c.tileAt(-1, 0);
  }

  @Test
  public void equals_hashCode_differentNameOrCostOrValueNotEqual() {
    Card base = new SanguineCard("Plus", 2, 7, plusGrid());
    assertNotEquals(base, new SanguineCard("Other", 2, 7, plusGrid()));
    assertNotEquals(base, new SanguineCard("Plus", 3, 7, plusGrid()));
    assertNotEquals(base, new SanguineCard("Plus", 2, 8, plusGrid()));
  }

  @Test
  public void accessors_nameCostValue() {
    Card c = new SanguineCard("N", 3, 9, plusGrid());
    assertEquals("N", c.name());
    assertEquals(3, c.cost());
    assertEquals(9, c.value());
  }
}
