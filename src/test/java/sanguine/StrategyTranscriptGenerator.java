package sanguine;

import java.util.List;
import sanguine.model.Card;
import sanguine.model.InfluenceGridTile;
import sanguine.model.Player;
import sanguine.model.moves.SanguineMove;
import sanguine.model.strategy.FillFirstStrategy;
import sanguine.model.strategy.MaxRowScoreStrategy;
import sanguine.model.strategy.MockReadOnlySanguineModel;

/**
 * Helper class to generate text transcripts for strategies.
 * Run this main(), then copy the printed logs into .txt files.
 */
public final class StrategyTranscriptGenerator {

  private StrategyTranscriptGenerator() {
  }

  /**
   * run to generate transcripts for strategies.
   *
   * @param args command line arguments (has no affect)
   */
  public static void main(String[] args) {
    generateFillFirstTranscript();
    System.out.println("==================================================");
    generateMaxRowScoreTranscript();
  }

  private static void generateFillFirstTranscript() {
    MockReadOnlySanguineModel mock = new MockReadOnlySanguineModel(5, 3);

    mock.setHand(Player.RED, List.of(
        new TranscriptCard("A", 2),
        new TranscriptCard("B", 3),
        new TranscriptCard("C", 4)));

    mock.addLegalMove(Player.RED, 0, 2, 4);

    FillFirstStrategy strat = new FillFirstStrategy();
    SanguineMove move = strat.chooseMove(mock, Player.RED);

    System.out.println("FILL-FIRST STRATEGY");
    System.out.println("Chosen move: " + move.getClass().getSimpleName());
    System.out.println("fill-first transcript:");
    System.out.print(mock.getLog());
  }

  private static void generateMaxRowScoreTranscript() {
    MockReadOnlySanguineModel mock = new MockReadOnlySanguineModel(5, 3);

    mock.setHand(Player.RED, List.of(
        new TranscriptCard("A", 2),
        new TranscriptCard("B", 3)));

    mock.setRowScore(Player.RED, 0, 2);
    mock.setRowScore(Player.BLUE, 0, 4);

    mock.setRowScore(Player.RED, 1, 5);
    mock.setRowScore(Player.BLUE, 1, 3);

    mock.setRowScore(Player.RED, 2, 0);
    mock.setRowScore(Player.BLUE, 2, 0);

    mock.addLegalMove(Player.RED, 1, 0, 2);

    MaxRowScoreStrategy strat = new MaxRowScoreStrategy();
    SanguineMove move = strat.chooseMove(mock, Player.RED);

    System.out.println("MAX-ROW-SCORE STRATEGY");
    System.out.println("Chosen move: " + move.getClass().getSimpleName());
    System.out.println("max-row-score transcript:");
    System.out.print(mock.getLog());
  }

  /**
   * Tiny Card implementation for transcripts; strategies only care about value().
   */
  private static final class TranscriptCard implements Card {

    private final String name;
    private final int value;

    TranscriptCard(String name, int value) {
      this.name = name;
      this.value = value;
    }

    @Override
    public String name() {
      return this.name;
    }

    @Override
    public int cost() {
      return 1;
    }

    @Override
    public int value() {
      return this.value;
    }

    @Override
    public InfluenceGridTile tileAt(int row, int col) {
      throw new UnsupportedOperationException("tileAt not used for transcripts");
    }
  }
}
