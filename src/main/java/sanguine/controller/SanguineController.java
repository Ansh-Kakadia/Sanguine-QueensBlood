package sanguine.controller;

import sanguine.view.SanguineEventListener;

/**
 * behaviors necessary to control a game of Sanguine. implementations must be able to respond
 * to GUI events defined by the {@link SanguineEventListener} interface.
 */
public interface SanguineController extends SanguineEventListener, GameStateListener {


}
