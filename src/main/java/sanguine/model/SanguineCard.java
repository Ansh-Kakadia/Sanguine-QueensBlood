package sanguine.model;

import java.util.List;

/**
 * represents a card in a game of sanguine. this card has a 5x5 influence grid, as well as a
 * name, cost (in pawns), and value.
 */
public class SanguineCard implements Card {

  private final String name;
  private final int cost;
  private final int value;
  private final List<List<InfluenceGridTile>> grid;

  /**
   * constructs a {@link SanguineCard} with the given parameters.
   *
   * @param name  the name of the card
   * @param cost  the cost of the card
   * @param value the value of the card
   * @param grid  the influence grid of the card (as viewed by red)
   */
  public SanguineCard(String name, int cost, int value, List<List<InfluenceGridTile>> grid) {
    if (name == null) {
      throw new IllegalArgumentException("name is null");
    }

    if (cost < 1 || cost > 3) {
      throw new IllegalArgumentException("cost is out of range [1,3]: " + cost);
    }

    if (value <= 0) {
      throw new IllegalArgumentException("value not positive: " + value);
    }

    if (grid.size() != 5 || !grid.stream().allMatch(row -> row.size() == 5)) {
      throw new IllegalArgumentException("grid size not 5x5.");
    }
    if (grid.get(2).get(2) != InfluenceGridTile.PLACED) {
      throw new IllegalArgumentException("PLACED must be in the center of the grid");
    }

    for (int row = 0; row < 5; row++) {
      for (int col = 0; col < 5; col++) {
        if (!(row == 2 && col == 2) && grid.get(row).get(col) == InfluenceGridTile.PLACED) {
          throw new IllegalArgumentException("PLACED must be in the center of the grid");
        }
      }
    }

    this.name = name;
    this.cost = cost;
    this.value = value;
    this.grid = grid.stream().map(row -> row.stream().map(tile -> tile).toList()).toList();
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public int cost() {
    return cost;
  }

  @Override
  public int value() {
    return value;
  }

  @Override
  public InfluenceGridTile tileAt(int row, int col) throws IllegalArgumentException {
    if (row < 0 || row >= grid.size()) {
      throw new IllegalArgumentException("row out of bounds");
    }
    if (col < 0 || col >= grid.get(row).size()) {
      throw new IllegalArgumentException("column out of bounds");
    }
    return grid.get(row).get(col);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Card other)) {
      return false;
    }

    if (this.value() != other.value() || !this.name().equals(other.name())
        || this.cost() != other.cost()) {
      return false;
    }


    for (int row = 0; row < 5; row++) {
      for (int col = 0; col < 5; col++) {
        if (this.tileAt(row, col) != other.tileAt(row, col)) {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 17;
    hashCode = 31 * hashCode + name().hashCode();
    hashCode = 31 * hashCode + Integer.hashCode(cost());
    hashCode = 31 * hashCode + Integer.hashCode(value());
    for (int r = 0; r < 5; r++) {
      for (int c = 0; c < 5; c++) {
        hashCode = 31 * hashCode + tileAt(r, c).hashCode();
      }
    }
    return hashCode;
  }

  @Override
  public String toString() {
    StringBuilder ret = new StringBuilder();
    ret.append(name).append(" ").append(cost).append(" ").append(value).append("\n");
    for (int row = 0; row < 5; row += 1) {
      for (int col = 0; col < 5; col += 1) {
        ret.append(tileAt(row, col).toString());
      }
      ret.append("\n");
    }
    return ret.toString();
  }
}
