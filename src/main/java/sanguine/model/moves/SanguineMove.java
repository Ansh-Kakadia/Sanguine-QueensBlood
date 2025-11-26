package sanguine.model.moves;

import sanguine.model.SanguineModel;

/**
 * represents a move in a game of sanguine.Sanguine. The moves can be made on a model with the
 * {@code affect} method.
 */
public interface SanguineMove {

  /**
   * makes the move on the given model.
   *
   * @param model the model this move should be made on
   * @throws IllegalArgumentException if this move is illegal, or if the model is {@code null}
   */
  void affect(SanguineModel model) throws IllegalArgumentException;
}
