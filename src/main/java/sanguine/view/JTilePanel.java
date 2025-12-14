package sanguine.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import sanguine.model.GameTile;
import sanguine.model.Player;
import sanguine.model.ReadOnlySanguineModel;

/**
 * represents a {@link JPanel} representing a {@link GameTile}. registered listeners have
 * {@code tileClicked} called on them when the panel is clicked
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public abstract class JTilePanel extends JPanel implements TilePanel {

  private final List<SanguineEventListener> listeners;
  private final int row;
  private final int col;
  private boolean highlighted;

  /**
   * constructs a {@link JTilePanel} representing the tile when given information about it.
   *
   * @param row the row this is in the grid
   * @param col the column this is in the grid
   * @throws IllegalArgumentException if the tile is {@code null}, or if any coordinate is negative
   */
  public JTilePanel(int row, int col) {
    super();
    if (row < 0 || col < 0) {
      throw new IllegalArgumentException("Invalid Coordinates");
    }

    highlighted = false;
    this.row = row;
    this.col = col;

    listeners = new ArrayList<>();
    setBorder(new LineBorder(Color.BLACK, 3));

    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        handleClick();
      }
    });
  }

  @Override
  public void setClick(boolean highlight) {
    highlighted = highlight;
    repaint();
  }

  @Override
  public boolean clicked() {
    return highlighted;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    setBackground(highlighted ? Color.BLACK : backgroundColor());

  }

  @Override
  public void register(SanguineEventListener listener) {
    if (listener == null) {
      throw new IllegalArgumentException("listener cannot be null");
    }
    listeners.add(listener);
  }

  /**
   * customizes the background color of this panel. All subclasses must override.
   *
   * @return the background color of this panel
   */
  protected abstract Color backgroundColor();

  /**
   * behavior exhibited when this panel is clicked. Notifies all registered
   * {@link SanguineEventListener} of click using {@code tileClicked}.
   */
  protected void handleClick() {
    for (SanguineEventListener listener : listeners) {
      listener.tileClicked(row, col);
    }
  }
}
