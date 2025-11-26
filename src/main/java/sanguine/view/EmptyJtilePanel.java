package sanguine.view;

import java.awt.Color;
import javax.swing.JPanel;

/**
 * A {@link JPanel} that represents an empty tile in a board of a game of Sanguine. Has color
 * {@code Color.LIGHT_GRAY}, and no pawns, card, or owner.
 */
public class EmptyJtilePanel extends JTilePanel {

  /**
   * constructs a {@link JTilePanel} representing the empty tile.
   *
   * @param row the row this is in the grid
   * @param col the column this is in the grid
   * @throws IllegalArgumentException if the tile is {@code null}, or if any coordinate is negative
   */
  public EmptyJtilePanel(int row, int col) {
    super(row, col);
  }

  @Override
  protected Color backgroundColor() {
    return Color.LIGHT_GRAY;
  }

}
