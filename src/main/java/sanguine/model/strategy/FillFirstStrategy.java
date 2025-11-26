package sanguine.model.strategy;

import java.util.List;
import sanguine.model.Card;
import sanguine.model.Player;
import sanguine.model.ReadOnlySanguineModel;
import sanguine.model.moves.Pass;
import sanguine.model.moves.PlaceCard;
import sanguine.model.moves.SanguineMove;

/**
 * A strategy that always tries to place the earliest card in the hand on the
 * earliest legal board position, and passes if no such move exists.
 */
public class FillFirstStrategy implements SanguineStrategy {

  @Override
  public SanguineMove chooseMove(ReadOnlySanguineModel model, Player player)
      throws IllegalArgumentException {
    if (model == null || player == null) {
      throw new IllegalArgumentException("model and player must be non-null");
    }

    if (model.isGameOver()) {
      return new Pass();
    }

    List<Card> hand = model.getHand(player);
    int height = model.height();
    int width = model.width();

    for (int handIndex = 0; handIndex < hand.size(); handIndex++) {
      for (int row = 0; row < height; row++) {
        for (int col = 0; col < width; col++) {
          if (model.canPlayCard(player, handIndex, row, col)) {
            return new PlaceCard(row, col, handIndex);
          }
        }
      }
    }

    return new Pass();
  }
}
