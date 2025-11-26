package sanguine.view;

import sanguine.model.Card;
import sanguine.model.Player;
import sanguine.model.SanguineModel;

/**
 * Renders the text for the game.
 * Prints one line per row as:
 *
 * <p>{@code redRowScore} {@code cells} {@code blueRowScore}</p>
 * Cells:
 * '_' for empty
 * '1'/'2'/'3' for the number of pawns
 * 'R' for a card owned by RED
 * 'B' for a card owned by BLUE
 * Row scores:
 * sum of value() of cards owned by each player on that row.
 */
public final class SanguineTextView {

  /**
   * Renders the entire board + row-scores (one row per line).
   */
  public String render(SanguineModel m) {
    StringBuilder sb = new StringBuilder();
    int h = m.height();
    int w = m.width();

    for (int r = 0; r < h; r++) {
      int redScore = 0;
      int blueScore = 0;

      StringBuilder row = new StringBuilder();
      for (int c = 0; c < w; c++) {
        if (m.isCardAt(r, c)) {
          Player owner = m.ownerAt(r, c);
          Card card = m.cardAt(r, c);
          if (owner == Player.RED) {
            redScore += card.value();
            row.append('R');
          } else {
            blueScore += card.value();
            row.append('B');
          }
        } else {
          if (m.hasOwner(r, c)) {
            int count = m.pawnsAt(r, c);
            row.append((char) ('0' + count));
          } else {
            row.append('_');
          }
        }
      }

      sb.append(redScore).append(' ')
          .append(row)
          .append(' ')
          .append(blueScore);

      if (r < h - 1) {
        sb.append('\n');
      }
    }
    return sb.toString();
  }
}
