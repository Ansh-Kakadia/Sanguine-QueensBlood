package sanguine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import sanguine.model.InfluenceGridTile;
import sanguine.model.SanguineCard;

/**
 * a suite of tests for the constructor of the {@link SanguineCard} class. for tests of the
 * observers of the {@link SanguineCard} class, see {@link CardFileReaderTests}.
 */
public class SanguineCardConstructorTests {

  private List<List<InfluenceGridTile>> validGrid;
  private List<List<InfluenceGridTile>> unAffectedInMiddle;
  private List<List<InfluenceGridTile>> affectedInMiddle;

  private void init() {
    validGrid = new ArrayList<>();
    validGrid.add(new ArrayList<>(
        Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
            InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
            InfluenceGridTile.UNAFFECTED)));

    validGrid.add(new ArrayList<>(
        Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.AFFECTED,
            InfluenceGridTile.UNAFFECTED, InfluenceGridTile.AFFECTED,
            InfluenceGridTile.UNAFFECTED)));

    validGrid.add(new ArrayList<>(
        Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
            InfluenceGridTile.PLACED, InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED)));

    validGrid.add(new ArrayList<>(
        Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
            InfluenceGridTile.AFFECTED, InfluenceGridTile.AFFECTED, InfluenceGridTile.AFFECTED)));

    validGrid.add(new ArrayList<>(
        Arrays.asList(InfluenceGridTile.UNAFFECTED, InfluenceGridTile.AFFECTED,
            InfluenceGridTile.UNAFFECTED, InfluenceGridTile.UNAFFECTED,
            InfluenceGridTile.UNAFFECTED)));

    unAffectedInMiddle = validGrid.stream().map(ArrayList::new).collect(Collectors.toList());
    unAffectedInMiddle.get(2).set(2, InfluenceGridTile.UNAFFECTED);
    affectedInMiddle = validGrid.stream().map(ArrayList::new).collect(Collectors.toList());
    affectedInMiddle.get(2).set(2, InfluenceGridTile.AFFECTED);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAffectedInMiddleThrowsException() {
    init();
    new SanguineCard("hello", 1, 2, affectedInMiddle);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUnAffectedInMiddleThrowsException() {
    init();
    new SanguineCard("hello", 1, 2, unAffectedInMiddle);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNegativeCostThrowsException() {
    init();
    new SanguineCard("hello", -1, 1, validGrid);
  }

  @Test(expected = IllegalArgumentException.class)
  public void test0CostThrowsException() {
    init();
    new SanguineCard("hello", 0, 1, validGrid);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testHighCostThrowsException() {
    init();
    new SanguineCard("hello", 4, 1, validGrid);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNegativeValueThrowsException() {
    init();
    new SanguineCard("hello", 3, -1, validGrid);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNegative0ThrowsException() {
    init();
    new SanguineCard("hello", 3, 0, validGrid);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testExtraGridRowThrowsException() {
    init();
    validGrid.add(new ArrayList<>());
    for (int i = 0; i < 5; i++) {
      validGrid.get(5).add(InfluenceGridTile.UNAFFECTED);
    }
    new SanguineCard("hello", 1, 1, validGrid);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testExtraGridColThrowsException() {
    init();
    validGrid.get(2).add(InfluenceGridTile.UNAFFECTED);
    new SanguineCard("hello", 1, 2, validGrid);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNoPlacedInMiddleThrowsException() {
    init();
    validGrid.get(2).set(2, InfluenceGridTile.UNAFFECTED);
    validGrid.get(0).set(0, InfluenceGridTile.PLACED);
    new SanguineCard("hello", 1, 2, validGrid);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testExtraPlacedThrowsException() {
    init();
    System.out.println(validGrid);
    validGrid.get(0).set(0, InfluenceGridTile.PLACED);
    new SanguineCard("hello", 1, 2, validGrid);
  }
}
