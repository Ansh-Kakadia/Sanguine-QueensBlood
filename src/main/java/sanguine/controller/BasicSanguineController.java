package sanguine.controller;

import java.util.List;
import sanguine.model.Card;
import sanguine.model.Player;
import sanguine.model.SanguineModel;
import sanguine.view.SanguineView;

/**
 * A GUI controller for a single player in a game of Sanguine.
 * Listens to view events (card/tile clicks, place, pass)
 * and updates the shared model, enforcing turn order.
 */
public class BasicSanguineController implements SanguineController {

  private final SanguineModel model;
  private final SanguineView view;
  private final Player player;

  /**
   * Construct a controller for one player of Sanguine.
   *
   * @param model the shared game model
   * @param view  this player's view
   * @param player    which player this controller represents (RED or BLUE)
   */
  public BasicSanguineController(SanguineModel model, SanguineView view, Player player) {
    if (model == null || view == null || player == null) {
      throw new IllegalArgumentException("model, view, and player cannot be null");
    }
    this.model = model;
    this.view = view;
    this.player = player;
    this.view.register(this);
    this.model.register(this);
  }


  /**
   * Notified when a card in this player's view is clicked.
   * Relies on the view to track the selected card
   * and defers processing until {@link #placeCard()} is called.
   *
   * @param card the card that was clicked in the view
   * @throws IllegalArgumentException if the card is {@code null}
   */
  @Override
  public void cardClicked(Card card) {
    if (card == null) {
      throw new IllegalArgumentException("card cannot be null");
    }

    if (model.isGameOver()) {
      view.alertIllegalMove("Game is over");
      return;
    }

    if (model.getTurn() != player) {
      view.alertIllegalMove("Not your turn");
      return;
    }

    view.clickCard(card);
  }



  @Override
  public void tileClicked(int row, int col) {
    if (row >= model.height() || row < 0 || col >= model.width() || col < 0) {
      throw new IllegalArgumentException("row and/or col out of bounds");
    }

    if (model.isGameOver()) {
      view.alertIllegalMove("Game is over");
      return;
    }

    if (model.getTurn() != player) {
      view.alertIllegalMove("Not your turn");
      return;
    }

    view.clickTile(row, col);
  }

  @Override
  public void placeCard() {
    if (model.isGameOver()) {
      System.out.println("Game Over");
      return;
    }

    Player current = model.getTurn();

    if (current != player) {
      view.alertIllegalMove("Not Your Turn");
      return;
    }

    if (!view.hasClickedCard() || !view.hasClickedTile()) {
      view.alertIllegalMove("Cannot Place Card Without Clicked Card and Tile");
      return;
    }

    Card selectedCard = view.clickedCard();
    int[] tile = view.clickedTile();
    int row = tile[0];
    int col = tile[1];

    List<Card> hand = model.getHand(player);
    int indexInHand = hand.indexOf(selectedCard);
    if (indexInHand < 0) {
      view.alertIllegalMove("Cannot Place Card");
      return;
    }

    if (!model.canPlayCard(player, indexInHand, row, col)) {
      view.alertIllegalMove("Illegal Move!");
      return;

    }
    model.placeCard(indexInHand, row, col);
    view.refresh();
  }

  @Override
  public void pass() {
    Player current;
    try {
      current = model.getTurn();
    } catch (IllegalStateException e) {
      view.alertIllegalMove("Game is over");
      return;
    }

    if (current != player) {
      view.alertIllegalMove("Not your turn");
      return;
    }

    try {
      model.pass();
    } catch (IllegalStateException e) {
      view.alertIllegalMove(e.getMessage());
    }
  }

  @Override
  public void alertTurn(Player player) {
    view.refresh();
    if (player == this.player) {
      view.promptTurn();
    }
  }

  @Override
  public void gameOver() {
    view.displayGameEnd();
  }

  @Override
  public void startGame() {
    view.showImage();
  }
}
