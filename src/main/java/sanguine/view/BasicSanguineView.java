package sanguine.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import sanguine.model.Card;
import sanguine.model.Player;
import sanguine.model.ReadOnlySanguineModel;



/**
 * a view for a game of Sanguine using the {@link javax.swing} library.
 * ... Add more
 */
public class BasicSanguineView extends JFrame implements SanguineView, SanguineEventListener {

  private final ReadOnlySanguineModel model;
  private final List<SanguineEventListener> listeners;
  private final JPanel cardsPanel;
  private final List<JCardPanel> cardPanels;
  private final List<List<JTilePanel>> tilePanels;
  private final JPanel boardPanel;
  private final Player player;
  private Optional<Card> clickedCard;
  private Optional<int[]> clickedTile;

  /**
   * constructs a view for the given {@link ReadOnlySanguineModel}.
   *
   * @param model  the model to be rendered by this view
   * @param player the {@link Player} the view is for
   * @throws IllegalArgumentException if the given model is null
   */
  public BasicSanguineView(ReadOnlySanguineModel model, Player player)
      throws IllegalArgumentException {
    super();
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    if (model == null) {
      throw new NullPointerException("model is null");
    }
    this.model = model;
    this.player = player;


    setSize(800, 600);
    setBackground(Color.BLACK);
    listeners = new ArrayList<>();
    cardPanels = new ArrayList<>();
    tilePanels = new ArrayList<>();
    clickedCard = Optional.empty();
    clickedTile = Optional.empty();

    cardsPanel = new JPanel();
    cardsPanel.setLayout(new GridLayout(0, model.getMaxHandSize()));
    this.boardPanel = new JPanel(new GridLayout(model.height(), 2 + model.width()));

    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, boardPanel, cardsPanel);
    splitPane.setResizeWeight(0.75);
    splitPane.setDividerLocation(0.75);
    add(splitPane, BorderLayout.CENTER);

    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyChar() == 'p') {
          pass();
        } else if (e.getKeyChar() == 'c') {
          placeCard();
        }
      }
    });

    refresh();
    setMinimumSize(new Dimension(1200, 900));
    setExtendedState(JFrame.MAXIMIZED_BOTH);
    pack();
  }


  /**
   * refreshes the panels representing the cards in the player's hand such that they are up to
   * date with the model. All {@link JCardPanel}s will be unclicked.
   */
  protected void refreshHand() {
    cardsPanel.removeAll();
    cardPanels.clear();
    for (Card card : model.getHand(player)) {
      JCardPanel cardPanel = switch (player) {
        case RED -> new RedJCardPanel(card);
        case BLUE -> new BlueJCardPanel(card);
      };
      cardPanel.register(this);
      cardsPanel.add(cardPanel);
      cardPanels.add(cardPanel);
    }
  }


  /**
   * refreshes the panels representing the tiles on the game board or the score such that they
   * are up to date with the model.
   */
  protected void refreshGameBoard() {
    boardPanel.removeAll();
    tilePanels.clear();
    for (int row = 0; row < model.height(); row++) {
      boardPanel.add(scorePanel(row, Player.RED));
      List<JTilePanel> rowPanels = new ArrayList<>();
      for (int col = 0; col < model.width(); col++) {
        JTilePanel tilePanel = makeTilePanel(row, col);
        tilePanel.register(this);
        rowPanels.add(tilePanel);
        boardPanel.add(tilePanel);
      }
      tilePanels.add(rowPanels);
      boardPanel.add(scorePanel(row, Player.BLUE));
    }
  }

  /**
   * constructs and returns a new JTilePanel representing the tile at the given place in the model.
   *
   * @param row the row of the tile
   * @param col the column of the tile
   * @return the panel showing the tile at the given place in the model
   * @throws IllegalArgumentException if the coordinates are invalid according to
   *                                  {@link ReadOnlySanguineModel}
   */
  protected JTilePanel makeTilePanel(int row, int col) {
    if (row < 0 || col < 0 || row >= model.height() || col >= model.width()) {
      throw new IllegalArgumentException("row or col is out of bounds");
    }
    if (!model.hasOwner(row, col)) {
      return new EmptyJtilePanel(row, col);
    }

    Player owner = model.ownerAt(row, col);
    if (model.isCardAt(row, col)) {
      return new CardJtilePanel(row, col, model.cardAt(row, col), owner);
    }

    return new PawnsJtilePanel(row, col, model.pawnsAt(row, col), owner);
  }

  /**
   * returns the score panel on the given players side of the board.
   *
   * @param row    the row of the panel
   * @param player the player whose side the panel is on
   * @return the score panel on the given players side of hte board
   * @throws IllegalArgumentException if the row is out of bounds
   */
  protected JPanel scorePanel(int row, Player player) {
    Player rowWinner = model.getRowWinner(row).orElse(null);
    Color c;
    if (rowWinner != player) {
      c = Color.LIGHT_GRAY;
    } else {
      c = switch (player) {
        case RED -> Color.RED;
        case BLUE -> Color.BLUE;
      };
    }

    JPanel scorePanel = new JPanel();
    scorePanel.setBackground(c);

    int score = model.getRowScore(player, row);
    JLabel scoreLabel = new JLabel(String.valueOf(score));
    scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
    scoreLabel.setForeground(Color.BLACK);
    scorePanel.add(scoreLabel);

    return scorePanel;
  }

  @Override
  public void register(SanguineEventListener listener) {
    if (listener == null) {
      throw new IllegalArgumentException("listener is null");
    }
    listeners.add(listener);
  }

  @Override
  public void showImage() {
    setVisible(true);
    setFocusable(true);
    requestFocusInWindow();
  }

  @Override
  public void refresh() {
    refreshGameBoard();
    refreshHand();
    revalidate();
    repaint();
  }

  @Override
  public boolean hasClickedCard() {
    return clickedCard.isPresent();
  }

  @Override
  public Card clickedCard() {
    if (clickedCard.isEmpty()) {
      throw new IllegalStateException("no clicked card");
    }
    return clickedCard.get();
  }

  @Override
  public boolean hasClickedTile() {
    return clickedTile.isPresent();
  }

  @Override
  public int[] clickedTile() {
    if (clickedTile.isEmpty()) {
      throw new IllegalStateException("no clicked tile");
    }
    return clickedTile.get();
  }

  @Override
  public void promptTurn() {
    JOptionPane.showMessageDialog(
        this,
        "Your turn is up!",
        "Turn Up",
        JOptionPane.PLAIN_MESSAGE
    );
  }

  @Override
  public void displayGameEnd() {
    Optional<Player> winner = model.getWinning();
    String winningString = winner.map(value -> value + " wins").orElse("tie game");
    JOptionPane.showMessageDialog(
        this,
        "Game Over, " + winningString + ", Score: " + model.getScore(),
        "Game Over",
        JOptionPane.INFORMATION_MESSAGE
    );
  }

  @Override
  public void alertIllegalMove(String reason) {
    JOptionPane.showMessageDialog(
        this,
        reason,
        "Illegal Move",
        JOptionPane.INFORMATION_MESSAGE
    );
  }


  @Override
  public void clickCard(Card card) {

    boolean cardWasAlreadyClicked = false;
    for (JCardPanel cardPanel : cardPanels) {
      if (cardPanel.getCard() == card && cardPanel.clicked()) {
        cardWasAlreadyClicked = true;
        break;
      }
    }

    for (JCardPanel cardPanel : cardPanels) {
      cardPanel.setClick(false);
    }

    if (!cardWasAlreadyClicked) {
      for (JCardPanel cardPanel : cardPanels) {
        if (cardPanel.getCard() == card) {
          cardPanel.setClick(true);
          clickedCard = Optional.of(card);
          return;
        }
      }
    } else {
      clickedCard = Optional.empty();
    }
  }

  @Override
  public void clickTile(int row, int col) {
    JTilePanel tile = tilePanels.get(row).get(col);
    boolean clickingAlreadyClicked = tile.clicked();

    for (int rowIdx = 0; rowIdx < model.height(); rowIdx += 1) {
      for (int colIdx = 0; colIdx < model.width(); colIdx += 1) {
        JTilePanel tilePanel = tilePanels.get(rowIdx).get(colIdx);
        tilePanel.setClick(false);
      }
    }

    clickedTile = (!clickingAlreadyClicked) ? Optional.of(new int[] {row, col}) : Optional.empty();
    tile.setClick(!clickingAlreadyClicked);

  }

  @Override
  public void cardClicked(Card card) {
    listeners.forEach(listener -> listener.cardClicked(card));
  }

  @Override
  public void tileClicked(int row, int col) {
    listeners.forEach(listener -> listener.tileClicked(row, col));
  }

  @Override
  public void placeCard() {
    listeners.forEach(SanguineEventListener::placeCard);
  }

  @Override
  public void pass() {
    listeners.forEach(SanguineEventListener::pass);
  }


}
