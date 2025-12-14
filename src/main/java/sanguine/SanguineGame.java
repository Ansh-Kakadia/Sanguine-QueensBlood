package sanguine;

import sanguine.controller.BasicSanguineController;
import sanguine.controller.GameStateListener;
import sanguine.controller.StubController;
import sanguine.controller.machineplayers.StrategyPlayer;
import sanguine.model.BasicSanguine;
import sanguine.model.CardFileReader;
import sanguine.model.Player;
import sanguine.model.SanguineModel;
import sanguine.model.strategy.FillFirstStrategy;
import sanguine.model.strategy.MaxRowScoreStrategy;
import sanguine.view.BasicSanguineView;

/**
 * a class with a {@link #main(String[])} method to run a stub controller for a game of sanguine.
 */
public class SanguineGame {

  /**
   * runs the game of sanguine with a {@link StubController} and {@link BasicSanguineView},
   * uses the deck from "./docs/examples.deck".
   *
   * @param args command line arguments, expected in the following order
   *
   *             <table>
   *              <tr>
   *                <th>Index in {@code args}</th>
   *                <th>Value</th>
   *              </tr>
   *             <tr>
   *                <th>0</th>
   *                <th>Number of rows on the board</th>
   *             </tr>
   *             <tr>
   *                 <th>1</th>
   *                 <th>Number of columns on the board</th>
   *             </tr>
   *             <tr>
   *                  <th>2</th>
   *                  <th>The path for the red deck's file (see {@link CardFileReader})</th>
   *             </tr>
   *             <tr>
   *                  <th>3</th>
   *                  <th>The path for the blue deck's file</th>
   *             </tr>
   *             <tr>
   *                  <th>4</th>
   *                  <th>The Player for Red
   *                    <ul>
   *                      <li>"Human" for a gui based player the user can control</li>
   *                      <li>"FillFirst" for a fill-first strategy player</li>
   *                      <li>"MaxRowScore" for the max-row-score strategy</li>
   *                    </ul>
   *                  </th>
   *             </tr>
   *             <tr>
   *                  <th>5</th>
   *                  <th>The Player for Blue</th>
   *             </tr>
   *             </table>
   * @throws IllegalArgumentException if the arguments don't follow the documentation
   */
  public static void main(String[] args) {
    if (args.length != 6) {
      throw new IllegalArgumentException("Invalid number of arguments");
    }
    int rows = numFromString(args[0], "Invalid row number formatting");
    int columns = numFromString(args[1], "Invalid column number formatting");
    String redDeckString = args[2];
    String blueDeckString = args[3];

    SanguineModel model;
    try {
      model = new BasicSanguine(columns, rows, 4, redDeckString, blueDeckString, false, null);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid board-size/deck arguments");
    }
    GameStateListener redPlayer = strategyFromString(args[4], "Invalid red player string",
        model, Player.RED);
    GameStateListener bluePlayer = strategyFromString(args[5], "Invalid blue player string",
        model, Player.BLUE);
    model.startGame();
  }

  /**
   * returns the number represented by the given string.
   *
   * @param numString    the number as a string
   * @param errorMessage the message that should accompany the {@link IllegalArgumentException} ,
   *                     should one be thrown
   * @return the int representation of {@code numString}
   * @throws IllegalArgumentException if {@code numString} has invalid formatting.
   *                                  Thrown with {@code errorMessage}
   */
  private static int numFromString(String numString, String errorMessage) {
    try {
      return Integer.parseInt(numString);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(errorMessage);
    }
  }

  /**
   * returns the player type represented by the given string.
   *
   * @param strategyString the player type as a string (as formatted in the docs of
   *                       {@link #main(String[])}
   * @param errorMessage   the message that should accompany the {@link IllegalArgumentException} ,
   *                       should one be thrown
   * @param model          the model the {@link GameStateListener} is working on
   * @param player         the player the {@link GameStateListener} is playing for
   * @return the {@link GameStateListener} representation of {@code strategyString}
   * @throws IllegalArgumentException if {@code numString} has invalid formatting.
   *                                  Thrown with {@code errorMessage}
   */
  private static GameStateListener strategyFromString(String strategyString, String errorMessage,
                                                      SanguineModel model, Player player) {
    return switch (strategyString) {
      case "Human" -> new BasicSanguineController(model,
          new BasicSanguineView(model, player), player);
      case "FillFirst" -> new StrategyPlayer(model, player, new FillFirstStrategy());
      case "MaxRowScore" -> new StrategyPlayer(model, player, new MaxRowScoreStrategy());
      default -> throw new IllegalArgumentException(errorMessage);
    };
  }

}