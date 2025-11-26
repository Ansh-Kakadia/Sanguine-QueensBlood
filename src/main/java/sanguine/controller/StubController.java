package sanguine.controller;

import java.util.List;
import sanguine.model.Card;
import sanguine.model.SanguineModel;
import sanguine.view.JTilePanel;
import sanguine.view.SanguineEventListener;
import sanguine.view.SanguineView;

/**
 * represents a stub controller for a game of sanguine. Can relay card and tile clicks to the view.
 */
public class StubController implements SanguineEventListener {

  SanguineModel model;
  SanguineView view;

  /**
   * constructs a new stub controller using the given model.
   *
   * @param model the model for the game of Sanguine
   * @param view  the view for the game of Sanguine
   * @throws IllegalArgumentException if the model or view is {@code null}
   */
  public StubController(SanguineModel model, SanguineView view) {
    if (model == null || view == null) {
      throw new IllegalArgumentException("Null view or model");
    }
    this.model = model;
    this.view = view;
    view.register(this);
    view.showImage();
  }

  @Override
  public void cardClicked(Card card) {
    view.clickCard(card);
    System.out.println("Card clicked: " + card);
  }

  @Override
  public void tileClicked(int row, int col) {
    view.clickTile(row, col);
    System.out.println("Tile clicked:  " + row + ", " + col);
  }

  @Override
  public void placeCard() {
    System.out.println("Place card");
  }

  @Override
  public void pass() {
    System.out.println("Pass");
  }
}
