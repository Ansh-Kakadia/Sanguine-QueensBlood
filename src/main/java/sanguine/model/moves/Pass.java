package sanguine.model.moves;

import sanguine.model.SanguineModel;

/**
 * represents passing in a game of sanguine.Sanguine. called with {@code affect} method.
 */
public class Pass implements SanguineMove {


  @Override
  public void affect(SanguineModel model) throws IllegalArgumentException {
    if (model == null) {
      throw new IllegalArgumentException("Null model");
    }
    try {
      model.pass();
    } catch (IllegalStateException e) {
      throw new IllegalArgumentException("cannot pass");
    }
  }
}
