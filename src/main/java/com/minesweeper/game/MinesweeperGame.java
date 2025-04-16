package com.minesweeper.game;


import com.minesweeper.config.GameConfiguration;
import com.minesweeper.domain.Cell;
import com.minesweeper.domain.Coordinates;
import com.minesweeper.domain.Grid;
import com.minesweeper.exception.InputCancelledException;
import com.minesweeper.handler.InputHandler;
import com.minesweeper.handler.MinePlacer;
import com.minesweeper.handler.OutputHandler;

/**
 * Orchestrates the Minesweeper game flow.
 * Manages interactions between input/output handlers, the grid, and game state.
 */
public class MinesweeperGame {

    private final InputHandler inputHandler;
    private final OutputHandler outputHandler;
    private final MinePlacer minePlacer;
    private Grid grid;
    private GameState gameState;

    // GameState enum to track the user’s progress throughout the game
    private enum GameState {
        INITIALIZING, IN_PROGRESS, WON, LOST
    }

    /**
     * Constructs a new MinesweeperGame with its dependencies.
     *
     * @param inputHandler  The handler for user input.
     * @param outputHandler The handler for displaying output.
     * @param minePlacer    The strategy for placing mines.
     */
    public MinesweeperGame(InputHandler inputHandler, OutputHandler outputHandler, MinePlacer minePlacer) {
        this.inputHandler = inputHandler;
        this.outputHandler = outputHandler;
        this.minePlacer = minePlacer;
        this.gameState = GameState.INITIALIZING; // Start in initializing state
    }

    /**
     * Runs the main game loop.
     */
    public void run() {
        boolean playAgain = true;
        outputHandler.displayWelcomeMessage();
        while (playAgain) {
            try {
                setupGame();
                playGameLoop();
                endGame();
            } catch (InputCancelledException e) {
                // User chose to quit - message handled in endGame or run exit
                playAgain = false; // Exit the main loop
            } catch (Exception e) {
                System.err.println("An unexpected error occurred: " + e.getMessage());
                playAgain = false; 
            }
            // Prompt to play again only if game finished normally (Win/Loss)
            if (playAgain && (gameState == GameState.WON || gameState == GameState.LOST)) { 
                 playAgain = inputHandler.promptPlayAgain();
            } else {
                // If game exited prematurely (e.g., error, quit signal already handled)
                playAgain = false; 
            }
        }
        // Display goodbye only once when the loop truly exits
        outputHandler.displayGoodbyeMessage();
    }

    /**
     * Sets up a new game: gets configuration, creates and initializes the grid.
     */
    private void setupGame() throws InputCancelledException {
        gameState = GameState.INITIALIZING;
        GameConfiguration config = inputHandler.getGameConfiguration();
        grid = new Grid(config.gridSize());
        grid.initialize(minePlacer, config.mineCount());

    }
    
    /**
     * Runs the main loop of a single game session.
     */
    private void playGameLoop() throws InputCancelledException {
        gameState = GameState.IN_PROGRESS;
        while (gameState == GameState.IN_PROGRESS) {
            outputHandler.displayGrid(grid);
            Coordinates move = inputHandler.getMoveInput(grid.getSize());
            processMove(move);
        }
    }

    /**
     * Processes a player's move by revealing the selected cell and updating the game state.
     *
     * @param coordinates The coordinates chosen by the player.
     */
    private void processMove(Coordinates coordinates) {
        Cell selectedCell = grid.getCell(coordinates); // Relies on getCell for bounds check

        if (selectedCell.isRevealed()) {
            outputHandler.displayAlreadyRevealedMessage();
            return; 
        }
        // Check if the cell is a mine
        if (selectedCell.isMine()) {
            gameState = GameState.LOST;
        } else {
            grid.revealCell(coordinates);
            outputHandler.displayAdjacentMineCount(selectedCell.getAdjacentMineCount());

            // Check for win condition
            if (grid.areAllNonMinesRevealed()) {
                gameState = GameState.WON;
            }
        }
    }

    /**
     * Handles the end of a game, displaying the final grid and result message.
     */
    private void endGame() {
        if (gameState == GameState.LOST) {
            revealAllMines(); 
            outputHandler.displayGrid(grid);
            outputHandler.displayMineHitMessage();
        } else if (gameState == GameState.WON) {
            outputHandler.displayGrid(grid); 
            outputHandler.displayWinMessage();
        }
    }
    
    /**
     * Helper method to reveal all mine locations, typically used at game over.
     */
    private void revealAllMines() {
        if (grid != null) {
            // Use the public method we added to Grid
            grid.revealAllMines(); 
        }
    }
} 