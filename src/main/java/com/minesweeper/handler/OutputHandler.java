package com.minesweeper.handler;

// Import necessary classes from new locations
import com.minesweeper.domain.Grid;

/**
 * Interface defining the contract for displaying game state, grid, and messages to the user.
 */
public interface OutputHandler {

    /**
     * Displays the initial welcome message to the player.
     */
    void displayWelcomeMessage();

    /**
     * Displays the current state of the Minesweeper grid.
     * Implementations should format the grid clearly, showing row/column headers,
     * hidden cells, revealed cells with adjacent mine counts, etc.
     *
     * @param grid The Grid object to display.
     */
    void displayGrid(Grid grid);

    /**
     * Prompts the user to enter their next move.
     * (e.g., "Select a square to reveal (e.g. A1): ")
     */
    void displayPromptForMove();

    /**
     * Displays a message indicating the player has hit a mine and lost the game.
     * (e.g., "Oh no, you detonated a mine! Game over.")
     */
    void displayMineHitMessage();

    /**
     * Displays a message indicating the player has successfully cleared the board and won.
     * (e.g., "Congratulations, you have won the game!")
     */
    void displayWinMessage();

    /**
     * Displays the result of revealing a safe cell.
     * (e.g., "This square contains X adjacent mines.")
     *
     * @param count The number of adjacent mines (0 or more).
     */
    void displayAdjacentMineCount(int count);

    /**
     * Displays a message indicating that the user's input was invalid.
     *
     * @param input The invalid input string provided by the user.
     * @param reason A brief explanation of why the input was invalid.
     */
    void displayInvalidInputMessage(String input, String reason);

    /**
     * Displays a prompt telling user cell is already revealed.
     * (e.g., "(Already revealed)")
     * Note: The actual reading of the response is handled by InputHandler.
     */
    void displayAlreadyRevealedMessage();
    /**
     * Displays a prompt asking the user if they want to play again.
     * (e.g., "Press any key to play again...") 
     * Note: The actual reading of the response is handled by InputHandler.
     */
    void displayPromptPlayAgain();

    /**
     * Displays a final goodbye message when the application exits.
     */
    void displayGoodbyeMessage();
} 