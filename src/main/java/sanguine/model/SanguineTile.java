package sanguine.model;

import java.util.Optional;

/**
 * represents a tile in a game of sanguine.Sanguine. This can have either a card or pawns on it.
 */
public class SanguineTile implements GameTile {

  private int pawns;
  // INVARIANT: pawns <= 3 is always true

  private Optional<Card> card;
  private Optional<Player> owner;

  /**
   * constructs a new sanguine tile with 0 pawns and no card.
   */
  public SanguineTile() {
    this.pawns = 0;
    this.card = Optional.empty();
    owner = Optional.empty();
  }

  @Override
  public boolean hasCard() {
    return this.card.isPresent();
  }

  @Override
  public boolean hasOwner() {
    return owner.isPresent();
  }

  @Override
  public void addCard(Card card, Player owner)
      throws IllegalArgumentException, IllegalStateException {
    if (hasCard()) {
      throw new IllegalStateException("tile already has card");
    }

    if (card == null || owner == null) {
      throw new IllegalArgumentException("given null argument");
    }

    this.card = Optional.of(card);
    this.owner = Optional.of(owner);
    pawns = 0;
  }


  @Override
  public Card getCard() throws IllegalStateException {
    if (!hasCard()) {
      throw new IllegalStateException("tile doesn't have card");
    }
    return this.card.get();
  }

  @Override
  public int pawns() {
    if (hasCard()) {
      throw new IllegalStateException("tile has card");
    }
    return this.pawns;
  }

  @Override
  public void addPawn(Player owner) {
    if (hasCard()) {
      throw new IllegalStateException("card present");
    }
    if (owner == null) {
      throw new IllegalArgumentException("given null argument");
    }
    // checks if owner is different from owner
    if (this.owner.stream().anyMatch(player -> player != owner)) {
      pawns = 1;
    } else {
      if (pawns <= 2) {
        pawns += 1;
      }
    }
    this.owner = Optional.of(owner);
  }

  @Override
  public Player owner() {
    if (owner.isEmpty()) {
      throw new IllegalStateException("no one owns this tile");
    }
    return owner.get();
  }
}
