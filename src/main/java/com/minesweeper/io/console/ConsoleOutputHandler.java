package com.minesweeper.io.console;

// Import necessary classes from new locations
import com.minesweeper.domain.Cell;
import com.minesweeper.domain.Coordinates;
import com.minesweeper.domain.Grid;
import com.minesweeper.handler.OutputHandler;

/**
 * Console implementation of the OutputHandler interface.
 * Prints game messages and the grid state to standard output.
 */
public class ConsoleOutputHandler implements OutputHandler {

    private static final String HIDDEN_CELL = "_";
    private static final String MINE_CELL = "*"; // Character for a revealed mine (though game usually ends)

    @Override
    public void displayWelcomeMessage() {
        System.out.println("Welcome to Minesweeper!");
        System.out.println(); // Add a blank line for spacing
    }

    @Override
    public void displayGrid(Grid grid) {
        int size = grid.getSize();

        // Print header row (column numbers)
        System.out.print("  "); // Indent for row labels
        for (int c = 0; c < size; c++) {
            System.out.printf(" %d", c + 1); // Use printf for spacing if numbers get large
        }
        System.out.println();

        // Print grid rows
        for (int r = 0; r < size; r++) {
            System.out.print((char) ('A' + r)); // Print row label (A, B, C...)
            System.out.print(" ");
            for (int c = 0; c < size; c++) {
                Cell cell = grid.getCell(new Coordinates(r, c));
                String displayChar = getCellDisplayCharacter(cell);
                System.out.printf(" %s", displayChar); // Use printf for consistent spacing
            }
            System.out.println();
        }
        System.out.println(); // Add a blank line after the grid
    }

    private String getCellDisplayCharacter(Cell cell) {
        if (!cell.isRevealed()) {
            return HIDDEN_CELL;
        } else {
            if (cell.isMine()) {
                // This typically shouldn't be shown during normal play as game ends,
                // but useful for displaying the final grid on loss.
                return MINE_CELL;
            } else {
                // Display adjacent mine count for revealed non-mine cells
                return String.valueOf(cell.getAdjacentMineCount());
            }
        }
    }

    @Override
    public void displayPromptForMove() {
        System.out.print("Select a square to reveal (e.g. A1): ");
    }

    @Override
    public void displayMineHitMessage() {
        System.out.println("Oh no, you detonated a mine! Game over.");
    }

    @Override
    public void displayWinMessage() {
        System.out.println("Congratulations, you have won the game!");
    }

    @Override
    public void displayAdjacentMineCount(int count) {
        System.out.println("This square contains " + count + " adjacent mines.");
    }

    @Override
    public void displayInvalidInputMessage(String input, String reason) {
        System.out.printf("Invalid input: '%s'. %s%n", input, reason);
    }

    @Override
    public void displayPromptPlayAgain() {
        System.out.println("Press any key to play again...");
    }

    @Override
    public void displayGoodbyeMessage() {
        System.out.println("Goodbye!");
    }
} 