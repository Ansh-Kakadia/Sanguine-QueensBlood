package sanguine.model;

import sanguine.controller.GameStateListener;

/**
 * behaviors needed to alert {@link GameStateListener}s of changes in game turns. register
 * listeners with the {@code register} method.
 */
public interface GameStateAlerter {

  /**
   * register a listener to this alerter, all registers listeners should be stored and alerted
   * when the turn changes.
   *
   * @param listener the listener to be registered to this object
   * @throws IllegalArgumentException if the listener is null
   */
  void register(GameStateListener listener);

}
