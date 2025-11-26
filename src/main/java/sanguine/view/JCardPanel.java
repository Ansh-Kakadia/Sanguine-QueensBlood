package sanguine.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import sanguine.model.Card;

/**
 * an abstract class for implementations of {@link CardPanel} as a {@link JPanel}. Can be put in a
 * row to show the hand of a player.
 *
 * <p>All subclasses <strong>MUST</strong> override the methods {@code backgroundColor} and
 * {@code horizFlipInfluenceGrid}.</p>
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public abstract class JCardPanel extends JPanel implements CardPanel {

  protected final Card card;
  protected final String name;
  protected final List<SanguineEventListener> listeners;
  protected boolean clicked;


  /**
   * constructs the panel representing the given card.
   *
   * @param card the card to be rendered by this panel
   * @throws IllegalArgumentException if the card does not have influence grid coordinates of 5x5,
   *                                  or if the card is null
   */
  public JCardPanel(Card card) {
    super();


    listeners = new ArrayList<>();
    clicked = false;

    if (card == null) {
      throw new IllegalArgumentException("card can't be null");
    }
    this.card = card;
    name = card.name();

    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        handleClick();
      }
    });
    setBackground(backgroundColor());
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setBorder(new EmptyBorder(10, 10, 10, 10));

    add(constructInfluenceGrid());
    add(constructInfoPanel());
  }

  /**
   * customizes the background color of a {@link JCardPanel}.
   * subclasses must override this method.
   *
   * @return the background color of the panel
   */
  protected abstract Color backgroundColor();

  private JPanel constructInfoPanel() {
    JPanel infoPanel = new JPanel();
    infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
    infoPanel.setBackground(backgroundColor());
    JLabel name = new JLabel(card.name());
    JLabel cost = new JLabel("Cost: " + card.cost());
    JLabel value = new JLabel("Value: " + card.value());

    name.setFont(new Font("Arial", Font.BOLD, 24));
    cost.setFont(new Font("Arial", Font.PLAIN, 18));
    value.setFont(new Font("Arial", Font.PLAIN, 18));

    infoPanel.add(name);
    infoPanel.add(cost);
    infoPanel.add(value);

    return infoPanel;
  }


  /**
   * constructs the {@link JPanel} for the influence grid.
   * subclasses must use the {@code horizFlipInfluenceGrid} method to decide whether to flip
   * the grid horizontally.
   *
   * @return the {@link JPanel} representing the influence grid
   */
  protected JPanel constructInfluenceGrid() {
    Function<Integer, Integer> flipColIndexIfNeeded;
    if (horizFlipInfluenceGrid()) {
      flipColIndexIfNeeded = i -> 4 - i;
    } else {
      flipColIndexIfNeeded = i -> i;
    }
    JPanel influencePanel = new JPanel();
    influencePanel.setLayout(new GridLayout(5, 5));

    for (int row = 0; row < 5; row++) {
      for (int column = 0; column < 5; column++) {
        JPanel tile = new JPanel();
        Color color = switch (card.tileAt(row, flipColIndexIfNeeded.apply(column))) {
          case AFFECTED -> Color.CYAN;
          case UNAFFECTED -> Color.DARK_GRAY;
          case PLACED -> Color.ORANGE;
        };

        tile.setBackground(color);
        tile.setBorder(new LineBorder(Color.BLACK, 3));
        influencePanel.add(tile);
      }
    }
    influencePanel.setBorder(new LineBorder(Color.BLACK, 5));

    return influencePanel;
  }


  /**
   * All subclasses must override this to decide whether the influence grid of the card is
   * flipped horizontally.
   *
   * @return whether to flip the influence grid of the card horizontally
   */
  protected abstract boolean horizFlipInfluenceGrid();


  @Override
  public void register(SanguineEventListener listener) {
    if (listener == null) {
      throw new IllegalArgumentException("listener can't be null");
    }
    listeners.add(listener);

  }

  @Override
  public boolean clicked() {
    return clicked;
  }

  @Override
  public void setClick(boolean clicked) {
    if (clicked) {
      setBackground(Color.WHITE);
    } else {
      setBackground(backgroundColor());
    }
    this.clicked = clicked;
  }

  /**
   * defines behaviors for when this panel is clicked.
   */
  protected void handleClick() {
    for (SanguineEventListener listener : listeners) {
      listener.cardClicked(card);
    }
  }

  @Override
  public Card getCard() {
    return card;
  }


}
