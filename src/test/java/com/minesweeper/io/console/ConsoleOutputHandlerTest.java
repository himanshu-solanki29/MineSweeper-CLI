package com.minesweeper.io.console;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.minesweeper.domain.Coordinates;
import com.minesweeper.domain.Grid;
import com.minesweeper.handler.MinePlacer;
import com.minesweeper.handler.OutputHandler;

/**
 * Unit tests for the ConsoleOutputHandler class.
 * Verifies that messages and the grid are printed correctly to the console.
 */
class ConsoleOutputHandlerTest {

    private final PrintStream originalSystemOut = System.out;
    private ByteArrayOutputStream testOutput;
    private OutputHandler outputHandler;

    @BeforeEach
    void setUp() {
        testOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOutput));
        outputHandler = new ConsoleOutputHandler();
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalSystemOut);
    }

    // Helper to get captured output as String
    private String getCapturedOutput() {
        return testOutput.toString(StandardCharsets.UTF_8);
    }

    @Test
    void displayWelcomeMessage_shouldPrintWelcomeMessage() {
        outputHandler.displayWelcomeMessage();
        String output = getCapturedOutput();
        // Check for core part of the message
        assertTrue(output.contains("Welcome to Minesweeper!"), "Output should contain welcome message");
        assertTrue(output.endsWith("\n") || output.endsWith("\r\n"), "Output should end with a newline"); 
    }

    @Test
    void displayPromptForMove_shouldPrintPrompt() {
        outputHandler.displayPromptForMove();
        String output = getCapturedOutput();
        // Check for specific prompt text from problem statement
        assertTrue(output.contains("Select a square to reveal (e.g. A1): "), "Output should contain move prompt");
        // Should probably not end with a newline to allow input on the same line
         assertFalse(output.endsWith("\n") || output.endsWith("\r\n"), "Output should not end with a newline"); 
    }

    @Test
    void displayMineHitMessage_shouldPrintGameOverMessage() {
        outputHandler.displayMineHitMessage();
        String output = getCapturedOutput();
        assertTrue(output.contains("Oh no, you detonated a mine! Game over."), "Output should contain mine hit message");
        assertTrue(output.endsWith("\n") || output.endsWith("\r\n"), "Output should end with a newline");
    }

    @Test
    void displayWinMessage_shouldPrintCongratulationsMessage() {
        outputHandler.displayWinMessage();
        String output = getCapturedOutput();
        assertTrue(output.contains("Congratulations, you have won the game!"), "Output should contain win message");
        assertTrue(output.endsWith("\n") || output.endsWith("\r\n"), "Output should end with a newline");
    }

    @Test
    void displayAdjacentMineCount_shouldPrintCorrectMessage() {
        int count = 3;
        outputHandler.displayAdjacentMineCount(count);
        String output = getCapturedOutput();
        assertTrue(output.contains("This square contains 3 adjacent mines."), "Output should contain adjacent mine count message");
        assertTrue(output.endsWith("\n") || output.endsWith("\r\n"), "Output should end with a newline");
    }
    
    @Test
    void displayAdjacentMineCount_shouldHandleZeroMines() {
        int count = 0;
        outputHandler.displayAdjacentMineCount(count);
        String output = getCapturedOutput();
        assertTrue(output.contains("This square contains 0 adjacent mines."), "Output should contain adjacent mine count message for zero mines");
        assertTrue(output.endsWith("\n") || output.endsWith("\r\n"), "Output should end with a newline");
    }

    @Test
    void displayInvalidInputMessage_shouldPrintInputAndReason() {
        String badInput = "X99";
        String reason = "Coordinates out of bounds.";
        outputHandler.displayInvalidInputMessage(badInput, reason);
        String output = getCapturedOutput();
        assertTrue(output.contains("Invalid input"), "Output should indicate invalid input");
        assertTrue(output.contains(badInput), "Output should contain the invalid input string");
        assertTrue(output.contains(reason), "Output should contain the reason");
        assertTrue(output.endsWith("\n") || output.endsWith("\r\n"), "Output should end with a newline");
    }
    
     @Test
    void displayPromptPlayAgain_shouldPrintMessage() {
        outputHandler.displayPromptPlayAgain();
        String output = getCapturedOutput();
        // Check for core part of the message from problem statement
        assertTrue(output.contains("Press enter key to play again..."), "Output should contain play again prompt");
        // Problem statement shows this on its own line
        assertTrue(output.endsWith("\n") || output.endsWith("\r\n"), "Output should end with a newline");
    }

    @Test
    void displayGoodbyeMessage_shouldPrintMessage() {
        outputHandler.displayGoodbyeMessage();
        String output = getCapturedOutput();
        assertTrue(output.contains("Goodbye"), "Output should contain goodbye message"); // Simple check
        assertTrue(output.endsWith("\n") || output.endsWith("\r\n"), "Output should end with a newline");
    }

    // --- Tests for displayGrid --- 

    private Grid setupGridForDisplayTest() {
        // Creates a predictable 3x3 grid
        // Mine at (0,0) - Revealed (e.g., game over state)
        // Cell (1,1) - Revealed, adjacent count 1
        // Cell (0,1) - Revealed, adjacent count 1
        // Cell (1,0) - Revealed, adjacent count 1
        // Others hidden
        Grid grid = new Grid(3);
        MinePlacer placer = (g, count) -> g.getCell(new Coordinates(0, 0)).placeMine();
        grid.initialize(placer, 1); // Places mine at (0,0) and calculates counts

        // Manually reveal some cells for the test state
        grid.getCell(new Coordinates(0, 0)).reveal(); // Reveal the mine
        grid.getCell(new Coordinates(0, 1)).reveal(); 
        grid.getCell(new Coordinates(1, 0)).reveal(); 
        grid.getCell(new Coordinates(1, 1)).reveal(); 
        
        // Expected State:
        //   1 2 3
        // A * 1 _ 
        // B 1 1 _ 
        // C _ _ _ 
        
        return grid;
    }

    @Test
    void displayGrid_shouldPrintCorrectFormat() {
        Grid grid = setupGridForDisplayTest();
        outputHandler.displayGrid(grid);
        String output = getCapturedOutput();
        output = output.replace("\r\n", "\n");
        assertTrue(output.contains("  1 2 3"), "Output missing header row");
        assertTrue(output.contains("A  * 1 _"), "Output missing row A content");
        assertTrue(output.contains("B  1 1 _"), "Output missing row B content");
        assertTrue(output.contains("C  _ _ _"), "Output missing row C content");
        assertTrue(output.endsWith("\n\n"), "Output should have two newlines at the end (one after last row, one blank line)");
    }
} 