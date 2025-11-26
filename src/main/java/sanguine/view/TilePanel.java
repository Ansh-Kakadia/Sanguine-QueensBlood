package sanguine.view;

import sanguine.model.GameTile;

/**
 * represents a panel showing a {@link GameTile} in a view for a game of Sanguine.
 * Clicks on this panel can be detected with {@link SanguineEventListener}s registered through the
 * {@code register} method.
 */
public interface TilePanel extends SanguineEventSender, ClickSwitch {

  // Maybe more implementation with controller coming up

}
