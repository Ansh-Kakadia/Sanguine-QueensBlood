package sanguine;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Test;
import sanguine.model.BasicSanguine;
import sanguine.model.Player;

/**
 * Verifies start-of-turn draw timing:
 * - After pass(): next player draws at start of their turn (if under cap & deck not empty).
 * - After placeCard(...): current player does NOT draw at end.
 * - Multiple getTurn() calls in the same turn do not double-draw.
 */
public class BasicSanguineDrawTimingTests {

  /**
   * A 5x5 with only center 'C' (PLACED), all others 'X'; cost=1, value=1.
   */
  private static String simpleCenterCardBlock(String name) {
    return name
        + " 1 1\n"
        + "XXXXX\n"
        + "XXXXX\n"
        + "XXCXX\n"
        + "XXXXX\n"
        + "XXXXX\n";
  }

  private static String repeatSimpleCards() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 10; i++) {
      sb.append(simpleCenterCardBlock("C" + i));
    }
    return sb.toString();
  }

  private static Path writeDeck(String content) throws IOException {
    Path p = Files.createTempFile("deck_", ".config");
    Files.writeString(p, content);
    p.toFile().deleteOnExit();
    return p;
  }

  @Test
  public void pass_triggersNextPlayerDraw_whenUnderCap() throws Exception {
    Path deckR = writeDeck(repeatSimpleCards());
    Path deckB = writeDeck(repeatSimpleCards());
    BasicSanguine m = new BasicSanguine(3, 3, 1, deckR.toString(),
        deckB.toString());

    m.placeCard(0, 1, 0);


    m.placeCard(0, 1, 2);

    int blueHandBefore = m.getHand(Player.BLUE).size();
    assertEquals(0, blueHandBefore);

    m.pass();

    assertEquals(Player.BLUE, m.getTurn());
    int blueHandAfter = m.getHand(Player.BLUE).size();
    assertEquals(1, blueHandAfter);
  }

  @Test
  public void place_doesNotDrawForCurrentPlayerAtEnd() throws Exception {
    Path deckR = writeDeck(repeatSimpleCards());
    Path deckB = writeDeck(repeatSimpleCards());
    BasicSanguine m = new BasicSanguine(3, 3, 1, deckR.toString(),
        deckB.toString());

    int redHandBefore = m.getHand(Player.RED).size();
    assertEquals(1, redHandBefore);

    m.placeCard(0, 1, 0);

    int redHandAfter = m.getHand(Player.RED).size();
    assertEquals(0, redHandAfter);
  }

  @Test
  public void getTurn_doesNotDoubleDrawInSameTurn() throws Exception {
    Path deckR = writeDeck(repeatSimpleCards());
    Path deckB = writeDeck(repeatSimpleCards());
    BasicSanguine m = new BasicSanguine(3, 3, 1, deckR.toString(),
        deckB.toString());

    m.placeCard(0, 1, 0);
    m.placeCard(0, 1, 2);

    assertEquals(Player.RED, m.getTurn());
    int redAfterFirstGetTurn = m.getHand(Player.RED).size();
    assertEquals(1, redAfterFirstGetTurn);

    assertEquals(Player.RED, m.getTurn());
    int redAfterSecondGetTurn = m.getHand(Player.RED).size();
    assertEquals(1, redAfterSecondGetTurn);
  }
}
