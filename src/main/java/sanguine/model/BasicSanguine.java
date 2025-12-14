package sanguine.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Stack;
import sanguine.controller.GameStateListener;
import sanguine.model.moves.SanguineMove;


/**
 * the behaviors needed to play aMR game of sanguine.Sanguine. To make a turn, a player can either
 * pass with {@code pass}, or can place a card with {@code placeCard}, or can make a move
 * implementing {@link SanguineMove} with the {@code makeMove} method.
 *
 * <p>The following are the rules for the width, height, and decks of the game:
 *   <ul>
 *     <li>The height must be a positive number</li>
 *     <li>The width must be greater than 1 and odd</li>
 *     <li>The decks combined must have enough cards to cover every tile of the board</li>
 *     <li>The decks must not contain 3 of the same card</li>
 *     <li>All pawns belong to at most one player at a time </li>.
 *     <li>Pawn count is in [0..3]. We cap at 3 through SanguineTile.addPawn(Player)</li>
 *   </ul>
 * </p>
 *
 * <p>Coordinates are represented as (row,col), being 0-indexed from the top
 * and left respectively.</p>
 */
public class BasicSanguine implements SanguineModel {

  private final int width;
  private final int height;
  private final int maxHandSize;
  private final Map<Player, List<Card>> hands;
  // INVARIANT: Lists in value set have maximum size maxHandSize

  private final List<GameStateListener> listeners;
  private final List<List<GameTile>> grid;
  private final Stack<Card> redDeck;
  private final Stack<Card> blueDeck;
  private Player turn;
  private int consecutivePasses;
  private boolean started = false;

  /**
   * constructs a {@link BasicSanguine} with the specified parameters. Deck formats are specified in
   * the documentation for {@link CardFileReader}.
   *
   * @param width            the width of the board
   * @param height           the height of the board
   * @param redDeckFilePath  the path for the file describing the red deck
   * @param blueDeckFilePath the path for the file specifying the blue deck
   * @throws IllegalArgumentException if any argument is void, if the files for either deck cannot
   *                                  be read, or if the width, height or decks does not match the
   *                                  rules specified in the documentation of {@link BasicSanguine}
   */
  public BasicSanguine(int width, int height, int handSize,
                       String redDeckFilePath, String blueDeckFilePath,
                       boolean shuffle, Long seed)
      throws IllegalArgumentException {
    hands = new HashMap<Player, List<Card>>();
    hands.put(Player.BLUE, new ArrayList<>());
    hands.put(Player.RED, new ArrayList<>());

    throwExceptionIfInvalidGridSize(width, height);

    this.width = width;
    this.height = height;

    try {
      this.redDeck = new Stack<>();
      redDeck.addAll(CardFileReader.read(redDeckFilePath).reversed());
      this.blueDeck = new Stack<>();
      blueDeck.addAll(CardFileReader.read(blueDeckFilePath).reversed());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(e.getMessage());
    }

    testDecks(redDeck, blueDeck);

    if (handSize <= 0) {
      throw new IllegalArgumentException("hand size less than 1");
    }
    if (handSize > redDeck.size() / 3 || handSize > blueDeck.size() / 3) {
      throw new IllegalArgumentException("hand size more than a third of deck size");
    }
    this.maxHandSize = handSize;

    grid = constructGrid(width, height);

    for (int row = 0; row < height; row++) {
      grid.get(row).get(0).addPawn(Player.RED);
      grid.get(row).get(width - 1).addPawn(Player.BLUE);
    }

    Random rng = (seed == null) ? new java.util.Random() : new java.util.Random(seed);
    if (shuffle) {
      java.util.Collections.shuffle(redDeck, rng);
      java.util.Collections.shuffle(blueDeck, rng);
    }
    turn = Player.RED;
    consecutivePasses = 0;
    dealOutCards();
    listeners = new ArrayList<>();
  }

  /**
   * constructs a {@link BasicSanguine} with the specified parameters. Deck formats are specified
   * in the documentation for {@link CardFileReader} and are shuffled before play.
   *
   * @param width            the width of the board
   * @param height           the height of the board
   * @param redDeckFilePath  the path for the file describing the red deck
   * @param blueDeckFilePath the path for the file specifying the blue deck
   * @throws IllegalArgumentException if any argument is void, if the files for either deck cannot
   *                                  be read, or if the width, height or decks does not match the
   *                                  rules specified in the documentation of {@link BasicSanguine}
   */
  public BasicSanguine(int width, int height, int handSize,
                       String redDeckFilePath, String blueDeckFilePath)
      throws IllegalArgumentException {
    this(width, height, handSize, redDeckFilePath, blueDeckFilePath, true, null);
  }

  private static void throwExceptionIfInvalidGridSize(int width, int height) {
    if (height <= 0) {
      throw new IllegalArgumentException("width less than 1");
    }
    if (width <= 1) {
      throw new IllegalArgumentException("height less than 2");
    }
    if (width % 2 == 0) {
      throw new IllegalArgumentException("width is even");
    }
  }

  private void dealOutCards() {
    for (int i = 0; i < maxHandSize; i++) {
      dealCardIfAllowed(Player.RED);
      dealCardIfAllowed(Player.BLUE);
    }
  }

  private void dealCardIfAllowed(Player player) {
    if (player == null) {
      throw new IllegalArgumentException("player is null");
    }

    Stack<Card> deck = switch (player) {
      case BLUE -> blueDeck;
      case RED -> redDeck;
    };

    if (hands.get(player).size() < maxHandSize && !deck.isEmpty()) {
      hands.get(player).add(deck.pop());
    }
  }

  private List<List<GameTile>> constructGrid(int width, int height) {
    final List<List<GameTile>> grid;
    grid = new ArrayList<>();
    for (int row = 0; row < height; row++) {
      List<GameTile> rowTiles = new ArrayList<>();
      for (int col = 0; col < width; col++) {
        rowTiles.add(new SanguineTile());
      }
      grid.add(rowTiles);
    }
    return grid;
  }

  @Override
  public int width() {
    return width;
  }

  @Override
  public int height() {
    return height;
  }

  @Override
  public Card cardAt(int row, int col) throws IllegalArgumentException, IllegalStateException {
    throwIllegalArgumentExceptionIfInvalidCoordinates(row, col);

    if (!isCardAt(row, col)) {
      throw new IllegalArgumentException("invalid coordinates");
    }

    return grid.get(row).get(col).getCard();
  }

  @Override
  public boolean isCardAt(int row, int col) throws IllegalArgumentException {
    throwIllegalArgumentExceptionIfInvalidCoordinates(row, col);
    return grid.get(row).get(col).hasCard();
  }

  @Override
  public int pawnsAt(int row, int col) throws IllegalArgumentException, IllegalStateException {
    throwIllegalArgumentExceptionIfInvalidCoordinates(row, col);
    if (isCardAt(row, col)) {
      throw new IllegalStateException("card at given tile");
    }
    return grid.get(row).get(col).pawns();
  }

  @Override
  public Player ownerAt(int row, int col)
      throws IllegalArgumentException, IllegalStateException {
    throwIllegalArgumentExceptionIfInvalidCoordinates(row, col);
    GameTile t = grid.get(row).get(col);
    if (!t.hasOwner()) {
      throw new IllegalStateException("tile has no owner");
    }
    return t.owner();
  }

  @Override
  public boolean isGameOver() {
    return grid.stream().allMatch(gameTiles -> gameTiles.stream().allMatch(GameTile::hasCard))
        || consecutivePasses >= 2;
  }

  @Override
  public void pass() throws IllegalStateException {
    if (isGameOver()) {
      throw new IllegalStateException("game over");
    }

    incrementTurn();
    consecutivePasses += 1;

    if (!isGameOver()) {
      dealCardIfAllowed(turn);
    }

    alertTurnListenersIfGameIsntOver();
    alertGameOverListenersIfNeeded();
  }


  @Override
  public void placeCard(int indexInHand, int row, int col)
      throws IllegalArgumentException, IllegalStateException {
    throwIllegalArgumentExceptionIfInvalidCoordinates(row, col);

    if (indexInHand < 0 || indexInHand >= hands.get(turn).size()) {
      throw new IllegalArgumentException("index out of bounds for hand");
    }

    Card card = hands.get(turn).get(indexInHand);
    if (card == null) {
      throw new IllegalArgumentException("null card");
    }
    if (isGameOver()) {
      throw new IllegalStateException("game over");
    }

    GameTile tile = grid.get(row).get(col);

    if (tile.hasCard()) {
      throw new IllegalStateException("card at given tile");
    }
    if (!tile.hasOwner() || tile.owner() != turn) {
      throw new IllegalStateException("other player owns this tile");
    }
    int cost = card.cost();
    if (tile.pawns() < cost) {
      throw new IllegalStateException("not enough pawns to cover card cost");
    }

    tile.addCard(card, turn);
    applyInfluence(card, row, col, turn);
    hands.get(turn).remove(indexInHand);
    consecutivePasses = 0;

    incrementTurn();

    if (!isGameOver()) {
      dealCardIfAllowed(turn);
    }

    alertTurnListenersIfGameIsntOver();
    alertGameOverListenersIfNeeded();
  }

  @Override
  public void makeTurn(SanguineMove move) throws IllegalArgumentException, IllegalStateException {
    if (move == null) {
      throw new IllegalArgumentException("move is null");
    }
    try {
      move.affect(this);
    } catch (IllegalArgumentException e) {
      throw new IllegalStateException("invalid move");
    }
  }

  @Override
  public Player getTurn() throws IllegalStateException {
    if (isGameOver()) {
      throw new IllegalStateException("Game over");
    }
    return turn;
  }

  @Override
  public List<Card> getHand(Player player) throws IllegalStateException {
    if (player == null) {
      throw new IllegalArgumentException("player is null");
    }
    List<Card> hand = hands.get(player);
    return new ArrayList<>(hand);
  }

  @Override
  public Optional<Player> getRowWinner(int row) throws IllegalArgumentException {
    if (row < 0 || row >= height) {
      throw new IllegalArgumentException("invalid row");
    }
    int[] rowScores = rowScores(row);
    int redScore = rowScores[0];
    int blueScore = rowScores[1];
    if (redScore > blueScore) {
      return Optional.of(Player.RED);
    }
    if (redScore < blueScore) {
      return Optional.of(Player.BLUE);
    }
    return Optional.empty();
  }

  @Override
  public int getScoreOfRow(int row) throws IllegalArgumentException {
    if (row < 0 || row >= height) {
      throw new IllegalArgumentException("invalid row");
    }
    int[] rowScores = rowScores(row);
    return Math.max(rowScores[0], rowScores[1]);
  }

  @Override
  public int getRowScore(Player player, int row) throws IllegalArgumentException {
    if (player == null) {
      throw new IllegalArgumentException("player is null");
    }
    if (row < 0 || row >= height) {
      throw new IllegalArgumentException("invalid row");
    }
    int[] scores = rowScores(row);
    return switch (player) {
      case RED -> scores[0];
      case BLUE -> scores[1];
    };
  }

  @Override
  public Optional<Player> getWinning() {
    Map<Player, Integer> scores = getScores();
    if (scores.get(Player.RED) > scores.get(Player.BLUE)) {
      return Optional.of(Player.RED);
    }
    if (scores.get(Player.BLUE) > scores.get(Player.RED)) {
      return Optional.of(Player.BLUE);
    }
    return Optional.empty();
  }

  private Map<Player, Integer> getScores() {
    Map<Player, Integer> scores = new HashMap<>();
    scores.put(Player.RED, 0);
    scores.put(Player.BLUE, 0);

    for (int row = 0; row < height; row++) {
      Optional<Player> player = getRowWinner(row);
      if (player.isPresent()) {
        scores.put(player.get(), scores.get(player.get()) + getScoreOfRow(row));
      }
    }
    return scores;
  }

  @Override
  public int getScore() {
    Map<Player, Integer> scores = getScores();
    return Math.max(scores.get(Player.RED), scores.get(Player.BLUE));
  }

  private int[] rowScores(int row) {
    int[] scores = {0, 0};
    for (GameTile tile : grid.get(row)) {
      if (tile.hasCard()) {
        switch (tile.owner()) {
          case RED:
            scores[0] += tile.getCard().value();
            break;
          case BLUE:
            scores[1] += tile.getCard().value();
            break;
          default:
            throw new IllegalArgumentException("invalid owner: " + tile.owner());
        }
      }
    }
    return scores;
  }

  private boolean validRow(int row) {
    return row < height() && row >= 0;
  }

  private boolean validColumn(int col) {
    return col < width() && col >= 0;
  }

  private boolean validCoordinates(int row, int col) {
    return validRow(row) && validColumn(col);
  }

  private void throwIllegalArgumentExceptionIfInvalidCoordinates(int row, int col)
      throws IllegalArgumentException {
    if (!validCoordinates(row, col)) {
      throw new IllegalArgumentException("invalid coordinates");
    }
  }

  @Override
  public boolean hasOwner(int row, int col) throws IllegalArgumentException {
    throwIllegalArgumentExceptionIfInvalidCoordinates(row, col);
    return grid.get(row).get(col).hasOwner();
  }

  @Override
  public boolean canPlayCard(Player player, int indexInHand, int row, int col) {
    if (player == null) {
      throw new IllegalArgumentException("player is null");
    }
    throwIllegalArgumentExceptionIfInvalidCoordinates(row, col);
    if (isGameOver()) {
      return false;
    }
    if (player != this.turn) {
      return false;
    }
    if (!hands.containsKey(player)) {
      return false;
    }
    if (indexInHand < 0 || indexInHand >= hands.get(player).size()) {
      return false;
    }

    Card card = hands.get(player).get(indexInHand);
    if (card == null) {
      return false;
    }

    GameTile tile = grid.get(row).get(col);
    if (tile.hasCard()) {
      return false;
    }
    if (!tile.hasOwner() || tile.owner() != player) {
      return false;
    }
    return tile.pawns() >= card.cost();
  }

  @Override
  public int getMaxHandSize() {
    return this.maxHandSize;
  }

  /**
   * switches turn from {@code RED} to {@code BLUE} or vice versa.
   */
  private void incrementTurn() {
    turn = switch (turn) {
      case RED -> Player.BLUE;
      case BLUE -> Player.RED;
    };
  }

  /**
   * throws {@link IllegalArgumentException} if the given decks are invalid.
   *
   * @param redDeck  the deck of the red player
   * @param blueDeck the deck of the blue player
   * @throws IllegalArgumentException if the given decks are invalid
   */
  private void testDecks(List<Card> redDeck, List<Card> blueDeck) throws IllegalArgumentException {
    if (redDeck.size() + blueDeck.size() < width * height) {
      throw new IllegalArgumentException("gameTiles must have at least " + width + " x " + height);
    }

    for (Card card : redDeck) {
      if (Collections.frequency(redDeck, card) > 2) {
        throw new IllegalArgumentException("card more than two of " + card + " in redDeck");
      }
    }

    for (Card card : blueDeck) {
      if (Collections.frequency(blueDeck, card) > 2) {
        throw new IllegalArgumentException("card more than two of " + card + "in blueDeck");
      }
    }
  }

  private void applyInfluence(Card card, int row, int col, Player who) {
    for (int gr = 0; gr < 5; gr++) {
      for (int gc = 0; gc < 5; gc++) {
        InfluenceGridTile t = card.tileAt(gr, gc);
        if (t == InfluenceGridTile.UNAFFECTED) {
          continue;
        }

        int dr = gr - 2;
        int dc = gc - 2;

        if (dr == 0 && dc == 0) {
          continue;
        }

        if (who == Player.BLUE) {
          dc = -dc;
        }

        int rr = row + dr;
        int cc = col + dc;
        if (!validCoordinates(rr, cc)) {
          continue;
        }

        GameTile gt = grid.get(rr).get(cc);

        if (gt.hasCard()) {
          continue;
        }
        if (!gt.hasOwner()) {
          gt.addPawn(who);
          continue;
        }
        if (gt.owner() == who) {
          gt.addPawn(who);
          continue;
        }
        int opponentCount = gt.pawns();

        for (int k = 0; k < opponentCount; k++) {
          gt.addPawn(who);
        }
      }
    }
  }

  /**
   * alerts the {@link GameStateListener}s that the turn has switched to the current {@code turn} if
   * the game isn't over.
   */
  protected void alertTurnListenersIfGameIsntOver() {
    if (!isGameOver()) {
      listeners.forEach(listener -> listener.alertTurn(turn));
    }
  }

  @Override
  public void register(GameStateListener listener) {
    if (listener == null) {
      throw new IllegalArgumentException("listener is null");
    }
    listeners.add(listener);
  }

  /**
   * alerts {@link GameStateListener}s that the game is over, with
   * {@link GameStateListener#gameOver()}, if the game is over.
   */
  protected void alertGameOverListenersIfNeeded() {
    if (isGameOver()) {
      listeners.forEach(listener -> listener.gameOver());
    }
  }

  @Override
  public void startGame() throws IllegalStateException {
    if (started) {
      throw new IllegalStateException("Game already started");
    }
    started = true;

    alertGameStateListenersStartGame();
    alertTurnListenersIfGameIsntOver();
  }

  /**
   * alerts all {@link GameStateListener}s registered to this model that the game has started
   * through the {@link GameStateListener#startGame()} method.
   */
  protected void alertGameStateListenersStartGame() {
    listeners.forEach(listener -> listener.startGame());
  }

}
