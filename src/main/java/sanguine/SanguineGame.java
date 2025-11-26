package sanguine;

import sanguine.controller.StubController;
import sanguine.model.BasicSanguine;
import sanguine.model.Player;
import sanguine.model.SanguineModel;
import sanguine.view.BasicSanguineView;
import sanguine.view.SanguineView;

/**
 * a class with a {@code main} method to run a stub controller for a game of sanguine.
 */
public class SanguineGame {

  /**
   * runs the game of sanguine with a {@link StubController} and {@link BasicSanguineView},
   * uses the deck from "./docs/examples.deck".
   *
   * @param args command line arguments, has no effect
   */
  public static void main(String[] args) {
    SanguineModel model =
        new BasicSanguine(7, 3, 4, "./docs/example.deck", "./docs/example.deck", false, null);


    // Create two views - one for each player
    SanguineView redView = new BasicSanguineView(model, Player.RED);
    SanguineView blueView = new BasicSanguineView(model, Player.BLUE);

    // Create controllers for each view
    StubController redController = new StubController(model, redView);
    StubController blueController = new StubController(model, blueView);

    // Show both views
    redView.showImage();
    blueView.showImage();
  }

}
