# Sanguine Queen's Blood

This repository contains the model, view, stub controller, and strategy layer for the game Sanguine.

The code lives in the `sanguine` package and is split into the `controller` `model` and `view` sub-packages.

## Controller

The current controller is a stub which prints the inputs from the view to the terminal. Inputs include
clicked cells and cards, or move attempts. The controller also tells the view to highlight the given
cell or card that clicked. 

## Model

The model uses the command pattern, with commands implementing the `SanguineMove` interface.
The model relies on objects implementing the `Card` and `GameTile` interfaces which represent cards and
cells in the game.

## View

The view uses the Swing library to render game states and take in user inputs. The codebase includes
the abstract classes `JTilePanel` and `JCardPanel` which can be extended to customize the look of the 
game. It also has event listener functionality to allow user input logic to be delegated to the controller.
Listeners (including the controller) must implement the `SanguineEventListener` interface. 

## Main

This repository includes a main function that constructs an example game using the stub controller for 
testing purposes.

## Deck Construction

Decks in the game are read from files. Example decks can be found in the `docs` directory, and information
about deck formatting can be found in the documentation for the `CardFileReader` class.
