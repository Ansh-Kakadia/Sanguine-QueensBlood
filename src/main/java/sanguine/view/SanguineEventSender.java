package sanguine.view;

/**
 * represents a sender of events to {@link SanguineEventListener}s. Lets users register listeners
 * with the {@code register} method.
 */
public interface SanguineEventSender {

  /**
   * registers a listener to be called in case of user actions.
   *
   * @param listener the listener to be registered
   * @throws IllegalArgumentException if the listener is null
   */
  void register(SanguineEventListener listener);

}
