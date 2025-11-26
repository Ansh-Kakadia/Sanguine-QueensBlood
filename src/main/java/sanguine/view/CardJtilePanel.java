package sanguine.view;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JPanel;
import sanguine.model.Card;
import sanguine.model.Player;


/**
 * a {@link JPanel} representing a {@link Card} in a game of Sanguine. displays the name cost and
 * value of the card.
 */
public class CardJtilePanel extends JTilePanel {

  private final Card card;
  private final Player owner;

  /**
   * constructs a {@link JTilePanel} representing the tile when given information about it.
   *
   * @param row  the row this is in the grid
   * @param col  the column this is in the grid
   * @param card the card this tile contains
   * @throws IllegalArgumentException if the card or player is {@code null}, or if any coordinate
   *                                  is negative
   *
   */
  public CardJtilePanel(int row, int col, Card card, Player owner) {
    super(row, col);
    if (card == null) {
      throw new IllegalArgumentException("Card is null");
    }
    if (owner == null) {
      throw new IllegalArgumentException("Owner is null");
    }
    this.card = card;
    this.owner = owner;
  }

  @Override
  protected Color backgroundColor() {
    return switch (owner) {
      case BLUE ->  Color.BLUE;
      case RED -> Color.RED;
    };
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    drawCardInfo(g);
  }

  private void drawCardInfo(Graphics g) {
    g.setColor(Color.WHITE);

    int width = getWidth();
    int height = getHeight();

    Font nameFont = new Font("Arial", Font.BOLD, 14);
    Font infoFont = new Font("Arial", Font.PLAIN, 12);

    g.setFont(nameFont);
    String name = card.name();
    int nameWidth = g.getFontMetrics().stringWidth(name);
    g.drawString(name, (width - nameWidth) / 2, 20);

    g.setFont(infoFont);
    String costText = "Cost: " + card.cost();
    g.drawString(costText, 10, height - 10);

    String valueText = "Value: " + card.value();
    int valueWidth = g.getFontMetrics().stringWidth(valueText);
    g.drawString(valueText, width - valueWidth - 10, height - 10);
  }
}
