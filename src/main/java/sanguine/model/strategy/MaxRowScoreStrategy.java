package sanguine.model.strategy;

import java.util.List;
import sanguine.model.Card;
import sanguine.model.Player;
import sanguine.model.ReadOnlySanguineModel;
import sanguine.model.moves.Pass;
import sanguine.model.moves.PlaceCard;
import sanguine.model.moves.SanguineMove;

/**
 * A strategy that looks for a move that will flip a row in the player's favor.
 * Only considers rows where the player is not currently ahead, and tries to
 * place a card so that the player's row score becomes strictly greater than the
 * opponent's. If no such move exists, it passes.
 */
public class MaxRowScoreStrategy implements SanguineStrategy {

  @Override
  public SanguineMove chooseMove(ReadOnlySanguineModel model, Player player)
      throws IllegalArgumentException {
    if (model == null || player == null) {
      throw new IllegalArgumentException("model and player must be non-null");
    }

    if (model.isGameOver()) {
      return new Pass();
    }

    Player opp = (player == Player.RED) ? Player.BLUE : Player.RED;
    List<Card> hand = model.getHand(player);
    int height = model.height();
    int width = model.width();

    for (int row = 0; row < height; row++) {
      int myScore = model.getRowScore(player, row);
      int oppScore = model.getRowScore(opp, row);

      if (myScore > oppScore) {
        continue;
      }

      for (int handIndex = 0; handIndex < hand.size(); handIndex++) {
        Card card = hand.get(handIndex);

        for (int col = 0; col < width; col++) {
          if (!model.canPlayCard(player, handIndex, row, col)) {
            continue;
          }

          int newMyScore = myScore + card.value();

          if (newMyScore > oppScore) {
            return new PlaceCard(row, col, handIndex);
          }
        }
      }
    }

    return new Pass();
  }
}
