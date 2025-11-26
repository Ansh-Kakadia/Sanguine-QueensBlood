package sanguine.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * a class meant to read .config files representing decks of {@link Card}s. The class only has one
 * static method {@code read} that takes in the file path and returns the list of cards.
 *
 * <p>Correct format for these files is a follows:
 *
 *   <ol>
 *     <li>The first line of each card should be CARD_NAME COST VALUE</li>
 *     <li>The next lines should represent the interaction grid with each line representing
 *     a row</li>
 *          <p>Each row should contain one of 3 symbols
 *        <ul>
 *          <li>X : This indicates the card has no influence on that cell.</li>
 *          <li>I : This indicates the card does have influence on that cell.</li>
 *          <li>C : This indicates the card's position. This must only exist in the middle of the
 *            grid on ROW_2 's third character. It cannot appear anywhere else.</li>
 *        </ul>
 *        </p>
 *     </li>
 *   </ol>
 * </p>
 * Note: this class only works for cards with 5x5 influence grids.
 */
public final class CardFileReader {


  /**
   * creates a file from the file at the given path. Format is specified in the class documentation.
   *
   * @param filePath the path of the file
   * @return the cards that the file contain
   * @throws IllegalArgumentException if the file does not exist, or if it's incorrectly formatted
   */
  public static List<Card> read(String filePath) throws IllegalArgumentException {
    List<Card> cards = new ArrayList<Card>();
    File file = new File(filePath);
    if (!file.exists()) {
      throw new IllegalArgumentException("File does not exist");
    }
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
      List<String> linesList = new ArrayList<>();
      String line;
      while ((line = br.readLine()) != null) {
        linesList.add(line);
      }
      String[] frLines = linesList.toArray(new String[0]);
      return runLoop(frLines);

    } catch (IOException e) {
      throw new IllegalArgumentException("Could not read file");
    }
  }

  private static List<Card> runLoop(String[] frLines) throws IOException {
    if (frLines.length % 6 != 0) {
      throw new IllegalArgumentException("invalid number of rows in the file");
    }
    List<Card> cards = new ArrayList<>();
    for (int idx = 0; idx < frLines.length; idx += 6) {
      cards.add(getCard(frLines, idx));
    }
    return cards;
  }

  // given the lines and start of the next line, returns the first card represented by the lines
  // starting at {@code firstLine} (0-indexed).
  private static Card getCard(String[] lines, int firstLine) throws IOException {
    if (firstLine + 5 >= lines.length) {
      throw new IllegalArgumentException("Invalid File Size");
    }
    String[] nameLineWords = lines[firstLine].split(" ");

    if (nameLineWords.length != 3) {
      throw new IllegalArgumentException("Illegal line " + firstLine);
    }
    String name = nameLineWords[0];
    int cost = Integer.parseInt(nameLineWords[1]);
    int value = Integer.parseInt(nameLineWords[2]);
    List<List<InfluenceGridTile>> grid = getGrid(lines, firstLine + 1);

    try {
      return new SanguineCard(name, cost, value, grid); // create card
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(e);
    }
  }

  private static List<List<InfluenceGridTile>> getGrid(String[] lines, int firstLine)
      throws IOException {
    List<List<InfluenceGridTile>> grid = new ArrayList<>();
    for (int idx = 0; idx <= 4; idx += 1) {
      grid.add(getGridRow(lines[firstLine + idx]));
    }
    return grid;
  }

  private static List<InfluenceGridTile> getGridRow(String line) throws IOException {
    List<InfluenceGridTile> row = new ArrayList<>();
    for (char character : line.toCharArray()) {
      row.add(
          switch (character) {
            case 'X' -> InfluenceGridTile.UNAFFECTED;
            case 'I' -> InfluenceGridTile.AFFECTED;
            case 'C' -> InfluenceGridTile.PLACED;
            default -> throw new IllegalArgumentException("Illegal character " + character);
          }
      );

    }
    return row;
  }
}
