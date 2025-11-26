package sanguine.view;

import java.awt.Color;
import sanguine.model.Card;

/**
 * represents a {@link JCardPanel} for the red player. Does <strong>NOT</strong>
 * flip influence grid. {@link SanguineEventListener}s can be registered with {@code register()}.
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class RedJCardPanel extends JCardPanel {


  /**
   * constructs the panel representing the given card.
   *
   * @param card the card to be rendered by this panel
   * @throws IllegalArgumentException if the card does not have influence grid coordinates of 5x5,
   *                                  or if the card is null
   */
  public RedJCardPanel(Card card) {
    super(card);
  }

  @Override
  protected Color backgroundColor() {
    return Color.RED;
  }

  @Override
  protected boolean horizFlipInfluenceGrid() {
    return false;
  }
}
