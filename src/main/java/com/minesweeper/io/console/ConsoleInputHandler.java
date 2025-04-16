package com.minesweeper.io.console;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.minesweeper.config.GameConfiguration;
import com.minesweeper.domain.Coordinates;
import com.minesweeper.exception.InputCancelledException;
import com.minesweeper.handler.InputHandler;
import com.minesweeper.handler.OutputHandler;

/**
 * Console implementation of the InputHandler interface.
 * Reads game configuration and player moves from standard input.
 */
public class ConsoleInputHandler implements InputHandler {

    private static final Pattern MOVE_PATTERN = Pattern.compile("^([A-Z])(\\d+)$"); // Pattern to match input like "A1", "B12", "Z26"
    private static final double MINES_MAX_DENSITY = 0.35; // Maximum density of mines on the grid
    private final Scanner scanner;
    private final OutputHandler outputHandler;

    /**
     * Primary constructor for dependency injection.
     * @param outputHandler Handler for displaying prompts/errors.
     * @param scanner Scanner instance to read input from.
     */
    public ConsoleInputHandler(OutputHandler outputHandler, Scanner scanner) {
        this.outputHandler = outputHandler;
        this.scanner = scanner;
    }

    /**
     * Convenience constructor using default System.in scanner.
     * @param outputHandler Handler for displaying prompts/errors.
     */
    public ConsoleInputHandler(OutputHandler outputHandler) {
        this(outputHandler, new Scanner(System.in));
    }

    @Override
    public GameConfiguration getGameConfiguration() throws InputCancelledException {
        int gridSize = readValidatedGridSize();
        int mineCount = readValidatedMineCount(gridSize);
        return new GameConfiguration(gridSize, mineCount);
    }

    private int readValidatedGridSize() throws InputCancelledException {
        int gridSize;
        while (true) {
            String rawInput = "";
            try {
                rawInput = readLine("Enter the size of the grid (e.g. 4 for a 4x4 grid): ");
                gridSize = Integer.parseInt(rawInput);
                if (gridSize <= 0) {
                    throw new IllegalArgumentException("Grid size must be positive.");
                }
                return gridSize; // valid grid size
            } catch (NumberFormatException e) {
                outputHandler.displayInvalidInputMessage(rawInput, "Input must be a valid integer.");
            } catch (IllegalArgumentException e) {
                outputHandler.displayInvalidInputMessage(rawInput, e.getMessage());
            }
        }
    }

    private int readValidatedMineCount(int gridSize) throws InputCancelledException {
        int mineCount;
        int totalCells = gridSize * gridSize;
        double maxMinesDouble = Math.floor(totalCells * MINES_MAX_DENSITY);
        int maxMines = (int) maxMinesDouble;

        while (true) { // Loop until a valid mine count is entered
            String prompt = String.format("Enter the number of mines to place on the grid (maximum is %d): ", maxMines);
            String rawInput = "";
            try {
                rawInput = readLine(prompt);
                mineCount = Integer.parseInt(rawInput);
                // Now, validate against rules (non-negative, <= totalCells, <= maxMines)
                if (mineCount < 0) {
                    throw new IllegalArgumentException("Mine count cannot be negative.");
                }
                if (mineCount > totalCells) {
                    throw new IllegalArgumentException("Mine count cannot exceed total cells (" + totalCells + ").");
                }
                if (mineCount > maxMines) {
                    throw new IllegalArgumentException("Mine count exceeds maximum allowed (" + maxMines + ").");
                }
                return mineCount; // Valid count entered
            } catch (NumberFormatException e) {
                outputHandler.displayInvalidInputMessage(rawInput, "Input must be a valid integer.");
            }  catch (IllegalArgumentException e) {
                outputHandler.displayInvalidInputMessage(rawInput, e.getMessage());
            }
        }
    }

    @Override
    public Coordinates getMoveInput(int gridSize) throws InputCancelledException {
        while (true) {
            outputHandler.displayPromptForMove();
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("quit")) {
                throw new InputCancelledException("User quit the game.");
            }

            Matcher matcher = MOVE_PATTERN.matcher(input);
            if (matcher.matches()) {
                try {
                    char rowChar = matcher.group(1).toUpperCase().charAt(0);
                    int col = Integer.parseInt(matcher.group(2));

                    int rowIndex = rowChar - 'A';
                    int colIndex = col - 1;

                    if (rowIndex >= 0 && rowIndex < gridSize && colIndex >= 0 && colIndex < gridSize) {
                        return new Coordinates(rowIndex, colIndex);
                    } else {
                        outputHandler.displayInvalidInputMessage(input,
                                String.format("Coordinates out of bounds for grid size %d. Row (A-%c), Column (1-%d).",
                                        gridSize, (char) ('A' + gridSize - 1), gridSize));
                    }
                } catch (NumberFormatException e) {
                    outputHandler.displayInvalidInputMessage(input, "Invalid column number format.");
                } catch (Exception e) { // Catch potential other issues like invalid row char
                    outputHandler.displayInvalidInputMessage(input, "Error parsing input: " + e.getMessage());
                }
            } else {
                outputHandler.displayInvalidInputMessage(input, "Invalid format. Use format like 'A1'.");
            }
        }
    }

    @Override
    public boolean promptPlayAgain() {
        outputHandler.displayPromptPlayAgain();
        System.out.println(" (Press Enter to play again, or type 'n' to quit)");
        try {
            String input = scanner.nextLine().trim().toLowerCase();
            return !input.equals("n"); // Anything other than 'n' (or empty string) means play again
        } catch (Exception e) {
            System.err.println("Error reading input for play again prompt: " + e.getMessage());
            return false;
        }
    }

    /**
     * Helper to read an integer, handling non-numeric input and 'quit'.
     */
    private String readLine(String prompt) throws InputCancelledException {
        System.out.print(prompt); // Keep direct print for prompt
        String line = scanner.nextLine().trim();
        if (line.equalsIgnoreCase("quit")) {
            throw new InputCancelledException("User quit during input.");
        }
        return line;
    }
}
 