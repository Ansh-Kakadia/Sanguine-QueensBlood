package sanguine.model.strategy;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import sanguine.model.Card;
import sanguine.model.Player;
import sanguine.model.ReadOnlySanguineModel;

/**
 * Test mock class for ReadOnlySanguineModel used by strategy tests.
 * Logs calls to key methods and returns configurable mock values.
 */
public class MockReadOnlySanguineModel implements ReadOnlySanguineModel {

  private final int width;
  private final int height;
  private final Map<Player, List<Card>> hands;
  private final Map<Player, Map<Integer, Integer>> rowScores;
  private final Set<MoveKey> legalMoves;
  private final StringBuilder log;
  private boolean gameOver;

  /**
   * Creates a mock model with the given board dimensions.
   * Hands, row scores, and legal moves start empty and can be configured
   * via the setter methods.
   */
  public MockReadOnlySanguineModel(int width, int height) {
    this.width = width;
    this.height = height;
    this.hands = new EnumMap<Player, List<Card>>(Player.class);
    this.rowScores = new EnumMap<Player, Map<Integer, Integer>>(Player.class);
    this.legalMoves = new HashSet<MoveKey>();
    this.log = new StringBuilder();
    this.gameOver = false;

    for (Player player : Player.values()) {
      this.hands.put(player, new ArrayList<Card>());
      this.rowScores.put(player, new HashMap<Integer, Integer>());
    }
  }

  private static final class MoveKey {
    private final Player player;
    private final int handIndex;
    private final int row;
    private final int col;

    MoveKey(Player player, int handIndex, int row, int col) {
      this.player = player;
      this.handIndex = handIndex;
      this.row = row;
      this.col = col;
    }

    @Override
    public boolean equals(Object other) {
      if (!(other instanceof MoveKey moveKey)) {
        return false;
      }
      return this.player == moveKey.player
          && this.handIndex == moveKey.handIndex
          && this.row == moveKey.row
          && this.col == moveKey.col;
    }

    @Override
    public int hashCode() {
      int hashcode = 17;
      hashcode = 31 * hashcode + this.player.hashCode();
      hashcode = 31 * hashcode + Integer.hashCode(this.handIndex);
      hashcode = 31 * hashcode + Integer.hashCode(this.row);
      hashcode = 31 * hashcode + Integer.hashCode(this.col);
      return hashcode;
    }
  }

  /**
   * Sets the mock hand for the given player to the provided list of cards.
   * Makes a defensive copy so later mutations to {@code cards} do not affect the mock.
   *
   * @param player player whose hand is being set
   * @param cards  cards to assign to that player's hand
   * @throws IllegalArgumentException if {@code player} is {@code null}
   */
  public void setHand(Player player, List<Card> cards) {
    if (player == null) {
      throw new IllegalArgumentException("player is null");
    }
    this.hands.put(player, new ArrayList<Card>(cards));
  }

  /**
   * Sets the mock row score for the given player on the given row.
   * Overwrites any previously stored score for that (player, row) pair.
   *
   * @param player player whose score is being configured
   * @param row    row index (0 indexed from  top)
   * @param score  score to store for that player on that row
   * @throws IllegalArgumentException if {@code player} is {@code null}
   */
  public void setRowScore(Player player, int row, int score) {
    if (player == null) {
      throw new IllegalArgumentException("player is null");
    }
    Map<Integer, Integer> perRow = this.rowScores.get(player);
    perRow.put(row, score);
  }

  /**
   * Marks the given player, hand index, row, column tuple as a legal move
   * for this mock model. Next calls to {@code canPlayCard} with the
   * same parameters will return {@code true}.
   *
   * @param player     player for who the move is legal
   * @param handIndex  index of the card in the player's hand
   * @param row        target row on the board (0-indexed from the top)
   * @param col        target column on the board (0-indexed from the left)
   */
  public void addLegalMove(Player player, int handIndex, int row, int col) {
    this.legalMoves.add(new MoveKey(player, handIndex, row, col));
  }

  public void setGameOver(boolean gameOver) {
    this.gameOver = gameOver;
  }

  public String getLog() {
    return this.log.toString();
  }

  @Override
  public int width() {
    return this.width;
  }

  @Override
  public int height() {
    return this.height;
  }

  @Override
  public boolean isGameOver() {
    this.log.append("isGameOver\n");
    return this.gameOver;
  }

  @Override
  public List<Card> getHand(Player player) {
    this.log.append("getHand ").append(player).append("\n");
    List<Card> cards = this.hands.get(player);
    if (cards == null) {
      return new ArrayList<Card>();
    }
    return new ArrayList<Card>(cards);
  }

  @Override
  public boolean canPlayCard(Player player, int indexInHand, int row, int col) {
    this.log.append("canPlayCard ")
        .append(player)
        .append(" idx=").append(indexInHand)
        .append(" row=").append(row)
        .append(" col=").append(col)
        .append("\n");
    MoveKey key = new MoveKey(player, indexInHand, row, col);
    return this.legalMoves.contains(key);
  }

  @Override
  public int getRowScore(Player player, int row) throws IllegalArgumentException {
    this.log.append("getRowScore ")
        .append(player)
        .append(" row=").append(row)
        .append("\n");
    Map<Integer, Integer> perRow = this.rowScores.get(player);
    if (perRow == null) {
      return 0;
    }
    Integer score = perRow.get(row);
    return score == null ? 0 : score;
  }

  @Override
  public int getMaxHandSize() {
    int max = 0;
    for (List<Card> cards : this.hands.values()) {
      if (cards.size() > max) {
        max = cards.size();
      }
    }
    return max;
  }

  @Override
  public Card cardAt(int row, int col) {
    throw new UnsupportedOperationException("cardAt not used in strategy tests");
  }

  @Override
  public boolean isCardAt(int row, int col) {
    throw new UnsupportedOperationException("isCardAt not used in strategy tests");
  }

  @Override
  public int pawnsAt(int row, int col) {
    throw new UnsupportedOperationException("pawnsAt not used in strategy tests");
  }

  @Override
  public Player ownerAt(int row, int col) {
    throw new UnsupportedOperationException("ownerAt not used in strategy tests");
  }

  @Override
  public boolean hasOwner(int row, int col) {
    throw new UnsupportedOperationException("hasOwner not used in strategy tests");
  }

  @Override
  public Player getTurn() {
    throw new UnsupportedOperationException("getTurn not used in strategy tests");
  }

  @Override
  public Optional<Player> getRowWinner(int row) {
    throw new UnsupportedOperationException("getRowWinner not used in strategy tests");
  }

  @Override
  public int getScoreOfRow(int row) {
    throw new UnsupportedOperationException("getScoreOfRow not used in strategy tests");
  }

  @Override
  public Optional<Player> getWinning() {
    throw new UnsupportedOperationException("getWinning not used in strategy tests");
  }

  @Override
  public int getScore() {
    throw new UnsupportedOperationException("getScore not used in strategy tests");
  }
}
