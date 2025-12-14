package sanguine.view;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;
import sanguine.model.Player;

/**
 * a {@link JPanel} representing a tile with pawns on it. the tile is colored based off the owner.
 */
public class PawnsJtilePanel extends JTilePanel {

  private final int pawns;
  private final Player owner;
  private static final int PAWN_DIAMETER = 10;

  /**
   * constructs a {@link JTilePanel} representing the tile when given information about it.
   *
   * @param row the row this is in the grid
   * @param col the column this is in the grid
   * @throws IllegalArgumentException if the tile is {@code null}, or if any coordinate is negative
   *                                  if the pawns are not in [1,3], or if the owner is {@code null}
   */
  public PawnsJtilePanel(int row, int col, int pawns, Player owner) {
    super(row, col);
    if (pawns < 1 || pawns > 3) {
      throw new IllegalArgumentException("pawns must be between 1 and 3");
    }
    this.pawns = pawns;
    if (owner == null) {
      throw new IllegalArgumentException("owner must not be null");
    }
    this.owner = owner;
  }

  @Override
  protected Color backgroundColor() {
    return switch (owner) {
      case RED -> Color.RED;
      case BLUE -> Color.BLUE;
    };
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    drawPawns(g);
  }

  private void drawPawns(Graphics g) {
    g.setColor(Color.WHITE);

    int width = getWidth();
    int height = getHeight();
    int centerX = width / 2;
    int centerY = height / 2;

    switch (pawns) {
      case 1 -> draw1Pawn(g, centerX, centerY);
      case 2 -> draw2Pawns(g, width, height);
      case 3 -> draw3Pawns(g, width, height);
      default ->  throw new IllegalArgumentException("invalid number of pawns: " + pawns);
    }
  }

  private void draw1Pawn(Graphics g, int centerX, int centerY) {
    g.fillOval(centerX - PAWN_DIAMETER / 2, centerY - PAWN_DIAMETER / 2,
        PAWN_DIAMETER, PAWN_DIAMETER);
  }



  private void draw2Pawns(Graphics g, int width, int height) {
    int y = height / 2 - PAWN_DIAMETER / 2;
    int leftX = width / 3 - PAWN_DIAMETER / 2;
    int rightX = 2 * width / 3 - PAWN_DIAMETER / 2;

    g.fillOval(leftX, y, PAWN_DIAMETER, PAWN_DIAMETER);
    g.fillOval(rightX, y, PAWN_DIAMETER, PAWN_DIAMETER);
  }

  private void draw3Pawns(Graphics g, int width, int height) {
    int topY = height / 3 - PAWN_DIAMETER / 2;
    int bottomY = 2 * height / 3 - PAWN_DIAMETER / 2;
    int centerX = width / 2 - PAWN_DIAMETER / 2;
    int leftX = width / 3 - PAWN_DIAMETER / 2;
    int rightX = 2 * width / 3 - PAWN_DIAMETER / 2;

    g.fillOval(centerX, topY, PAWN_DIAMETER, PAWN_DIAMETER);
    g.fillOval(leftX, bottomY, PAWN_DIAMETER, PAWN_DIAMETER);
    g.fillOval(rightX, bottomY, PAWN_DIAMETER, PAWN_DIAMETER);
  }
}
