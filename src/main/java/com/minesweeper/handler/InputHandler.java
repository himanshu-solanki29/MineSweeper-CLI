package com.minesweeper.handler;

import com.minesweeper.config.GameConfiguration;
import com.minesweeper.domain.Coordinates;
import com.minesweeper.exception.InputCancelledException;

/**
 * Interface defining the contract for handling user input for the Minesweeper game.
 * Implementations will provide ways to get game settings and player moves.
 */
public interface InputHandler {

    /**
     * Prompts the user and retrieves the desired game configuration (grid size and mine count).
     * Implementations should handle input validation (e.g., positive size, valid mine count range).
     *
     * @return A GameConfiguration object containing the validated settings.
     * @throws InputCancelledException if the user indicates they want to quit during setup.
     */
    GameConfiguration getGameConfiguration() throws InputCancelledException; 

    /**
     * Prompts the user to enter their next move (coordinates of the cell to reveal).
     * Implementations should handle parsing the input (e.g., "A1") into Coordinates
     * and validating the format and range based on the provided grid size.
     *
     * @param gridSize The current size of the grid, used for input validation.
     * @return The Coordinates chosen by the user.
     * @throws InputCancelledException if the user indicates they want to quit during the move input.
     */
    Coordinates getMoveInput(int gridSize) throws InputCancelledException;

    /**
     * Checks if the user wants to play again after a game ends.
     *
     * @return true if the user wants to play again, false otherwise.
     */
    boolean promptPlayAgain();
    
} 