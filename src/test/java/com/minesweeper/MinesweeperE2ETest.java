package com.minesweeper.game;

import com.minesweeper.domain.Coordinates;
import com.minesweeper.domain.Grid;
import com.minesweeper.handler.InputHandler;
import com.minesweeper.handler.MinePlacer;
import com.minesweeper.handler.OutputHandler;
import com.minesweeper.io.console.ConsoleInputHandler;
import com.minesweeper.io.console.ConsoleOutputHandler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Set; // Needed for the anonymous class logic
import java.util.HashSet; // Needed for the anonymous class logic

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-End test class for the Minesweeper Game.
 * Goal is to test if a user interacts with the game via the console using this sequence of inputs,
 * do they see the expected output and does the game reach the correct final state.
 */
public class MinesweeperE2ETest {

    private final InputStream systemIn = System.in;
    private final PrintStream systemOut = System.out;

    private ByteArrayOutputStream testOut;

    @BeforeEach
    public void setUpOutput() {
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
    }

    @AfterEach
    public void restoreSystemInputOutput() {
        System.setIn(systemIn);
        System.setOut(systemOut);
    }

    private void provideInput(String data) {
        ByteArrayInputStream testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }

    private String getOutput() {
        return testOut.toString().replace("\r\n", "\n");
    }

    @Test
    void testWinGameScenario() {
        // --- Setup ---
        String input = "3\n" + "1\n" + "A1\n" + "A2\n" + "A3\n" +
                "B1\n" +         "B3\n" + // Skip B2 (the mine)
                "C1\n" + "C2\n" + "C3\n" +
                "n\n";
        provideInput(input);

        // Define mine location(s) for this test
        final List<Coordinates> mineCoordinates = List.of(new Coordinates(1, 1)); // Mine at B2

        // Create MinePlacer using an Anonymous Inner Class
        MinesweeperGame game = getMinesweeperGame(mineCoordinates);
        game.run();

        // --- Assertions ---
        String output = getOutput();
        assertTrue(output.contains("Welcome to Minesweeper!"), "Missing welcome message");
        assertTrue(output.contains(
                "   1 2 3\n" +
                        "A  1 1 1\n" +
                        "B  1 _ 1\n" +
                        "C  1 1 1"), "Incorrect final grid display before win");
        assertTrue(output.contains("Congratulations, you have won the game!"), "Missing win message");
        assertTrue(output.contains("Goodbye!"), "Missing goodbye message");
    }


    @Test
    void testLossGameScenario() {
        // --- Setup ---
        String input = "3\n" + "1\n" + "B2\n" + "n\n";
        provideInput(input);

        // Mine at B2 (row 1, col 1)
        final List<Coordinates> mineCoordinates = List.of(new Coordinates(1, 1));
        MinesweeperGame game = getMinesweeperGame(mineCoordinates);
        // --- Execution ---
        game.run();

        // --- Assertions ---
        String output = getOutput();
        assertTrue(output.contains("Welcome to Minesweeper!"), "Missing welcome message");
        assertTrue(output.contains("Oh no, you detonated a mine! Game over."), "Missing loss message");
        assertTrue(output.contains(
                "1 2 3\n" +
                        "A  _ _ _\n" +
                        "B  _ * _\n" +
                        "C  _ _ _"), "Incorrect final grid display after loss");
        assertTrue(output.contains("Goodbye!"), "Missing goodbye message");
    }

    @Test
    void testCascadeRevealScenario() {
        // --- Setup ---
        String input = "4\n" + "1\n" + "A1\n" + "C4\n" + "D3\n" + "D4\n" +  "n\n";
        provideInput(input);
        // Mine at C3 (row 2, col 2)
        final List<Coordinates> mineCoordinates = List.of(new Coordinates(2, 2));
        MinesweeperGame game = getMinesweeperGame(mineCoordinates);

        // --- Execution ---
        game.run();

        // --- Assertions ---
        String output = getOutput();
        assertTrue(output.contains("Welcome to Minesweeper!"), "Missing welcome message");
        assertTrue(output.contains("This square contains 0 adjacent mines."), "Missing 0 adjacent mines message for A1");
        assertTrue(output.contains(
                "   1 2 3 4\n" +
                        "A  0 0 0 0\n" +
                        "B  0 1 1 1\n" +
                        "C  0 1 _ 1\n" +
                        "D  0 1 1 1"), "Incorrect grid display after cascade reveal"); // C3 (mine) remains hidden
        assertTrue(output.contains("Select a square to reveal"), "Should prompt for next move after cascade");
        assertTrue(output.contains("Congratulations, you have won the game!"), "Should show win message yet");
        assertTrue(output.contains("Goodbye!"), "Missing goodbye message");
    }

    private static MinesweeperGame getMinesweeperGame(List<Coordinates> mineCoordinates) {
        MinePlacer testMinePlacer = new MinePlacer() {
            private final Set<Coordinates> locations = new HashSet<>(mineCoordinates);

            @Override
            public void placeMines(Grid grid, int mineCount) {
                int placed = 0;
                for (Coordinates loc : locations) {
                    if (grid.isValidCoordinate(loc)) {
                            grid.getCell(loc).placeMine();
                            placed++;
                    }
                }
                assertEquals(mineCount, placed, "Test Placer Mismatch: Expected to place " + mineCount + " mines, but placed " + placed);
            }
        };

        // Console handlers
        OutputHandler outputHandler = new ConsoleOutputHandler();
        InputHandler inputHandler = new ConsoleInputHandler(outputHandler);

        // Create and run the game with the anonymous MinePlacer
        return new MinesweeperGame(inputHandler, outputHandler, testMinePlacer);
    }
}