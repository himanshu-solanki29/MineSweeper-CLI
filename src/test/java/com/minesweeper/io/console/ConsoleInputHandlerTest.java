package com.minesweeper.io.console;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.minesweeper.config.GameConfiguration;
import com.minesweeper.domain.Coordinates;
import com.minesweeper.exception.InputCancelledException;
import com.minesweeper.handler.OutputHandler;

/**
 * Unit tests for the ConsoleInputHandler class.
 * These tests simulate console input and verify parsing and validation.
 */
class ConsoleInputHandlerTest {

    private final InputStream originalSystemIn = System.in;
    private final PrintStream originalSystemOut = System.out;
    private ByteArrayOutputStream testOutput; // To capture System.out
    private OutputHandler mockOutputHandler; // Use a mock or real handler
    private ConsoleInputHandler inputHandler; // Instance under test - Use concrete type for constructor

    @BeforeEach
    void setUp() {
        testOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOutput));
        // Create the dependency (OutputHandler) needed by ConsoleInputHandler
        // Using the real ConsoleOutputHandler implementation here is okay since we capture its output
        mockOutputHandler = new ConsoleOutputHandler(); 
        // System.in will be set per test method before creating inputHandler
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalSystemIn);
        System.setOut(originalSystemOut);
    }

    private void provideInput(String data) {
        InputStream testInput = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
        System.setIn(testInput);
        inputHandler = new ConsoleInputHandler(mockOutputHandler, new Scanner(System.in)); 
    }

    // --- Tests for constructors ---
    @Test
    void constructor_WithOutputHandlerAndScanner_CreatesHandler() {
        inputHandler = new ConsoleInputHandler(mockOutputHandler, new Scanner(System.in));
        assertNotNull(inputHandler);
    }

    @Test
    void constructor_WithOutputHandlerOnly_CreatesHandler() {
        inputHandler = new ConsoleInputHandler(mockOutputHandler);
        assertNotNull(inputHandler);
    }

    // --- Tests for getMoveInput --- 
    @ParameterizedTest
    @CsvSource({
        "A1, 0, 0",
        "C3, 2, 2",
        "D4, 3, 3",
        "J10, 9, 9",
        "Z26, 25, 25"
    })
    void getMoveInput_shouldParseValidFormatsCorrectly(String input, int expectedRow, int expectedCol) throws InputCancelledException {
        provideInput(input + "\n"); 
        int gridSize = 26;
        Coordinates result = inputHandler.getMoveInput(gridSize);
        assertNotNull(result);
        assertEquals(expectedRow, result.row());
        assertEquals(expectedCol, result.column());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1A", "a2", "ab2", "A", "1", "", "A 1", "AA1", "A1A", "AA"})
    void getMoveInput_shouldRePromptOnInvalidFormat(String invalidInput) throws InputCancelledException {
        String validInput = "B2\n";
        provideInput(invalidInput + "\n" + validInput);
        int gridSize = 5;
        Coordinates result = inputHandler.getMoveInput(gridSize);
        assertNotNull(result);
        assertEquals(1, result.row());
        assertEquals(1, result.column());
        String output = testOutput.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Invalid format"));
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"A5", "E1", "D0", "D5"}) // For a 4x4 grid
    void getMoveInput_shouldRePromptOnOutOfBounds(String outOfBoundsInput) throws InputCancelledException {
        String validInput = "A1\n";
        provideInput(outOfBoundsInput + "\n" + validInput);
        int gridSize = 4;
        Coordinates result = inputHandler.getMoveInput(gridSize);
        assertNotNull(result);
        assertEquals(0, result.row());
        assertEquals(0, result.column());
        String output = testOutput.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("out of bounds"));
    }

    @Test
    void getMoveInput_shouldThrowExceptionOnQuit() {
        provideInput("quit\n");
        int gridSize = 5;
        assertThrows(InputCancelledException.class, () -> inputHandler.getMoveInput(gridSize));
    }
    
    // --- Tests for getGameConfiguration ---
    @Test
    void getGameConfiguration_shouldReturnCorrectConfig() throws InputCancelledException {
        provideInput("4\n3\n"); // Size 4, 3 mines
        inputHandler = new ConsoleInputHandler(mockOutputHandler, new Scanner(System.in));
        GameConfiguration config = inputHandler.getGameConfiguration();
        assertEquals(4, config.gridSize());
        assertEquals(3, config.mineCount());
    }

    @Test
    void getGameConfiguration_shouldRejectInvalidGridSize() throws InputCancelledException {
        provideInput("0\n-1\n4\n3\n"); // Invalid sizes followed by valid ones
        inputHandler = new ConsoleInputHandler(mockOutputHandler, new Scanner(System.in));
        GameConfiguration config = inputHandler.getGameConfiguration();
        assertEquals(4, config.gridSize());
        assertEquals(3, config.mineCount());
        String output = testOutput.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Grid size must be positive"));
    }

    @Test
    void getGameConfiguration_shouldRejectInvalidMineCount() throws InputCancelledException {
        provideInput("4\n-1\n17\n3\n"); // Invalid mine counts followed by valid one
        inputHandler = new ConsoleInputHandler(mockOutputHandler, new Scanner(System.in));
        GameConfiguration config = inputHandler.getGameConfiguration();
        assertEquals(4, config.gridSize());
        assertEquals(3, config.mineCount());
        String output = testOutput.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Mine count cannot be negative"));
        assertTrue(output.contains("Mine count cannot exceed total cells"));
    }

    @Test
    void getGameConfiguration_shouldRejectMineCountExceedingMaxDensity() throws InputCancelledException {
        provideInput("4\n10\n3\n"); // Mine count 10 exceeds max density for 4x4 grid
        inputHandler = new ConsoleInputHandler(mockOutputHandler, new Scanner(System.in));
        GameConfiguration config = inputHandler.getGameConfiguration();
        assertEquals(4, config.gridSize());
        assertEquals(3, config.mineCount());
        String output = testOutput.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Mine count exceeds maximum allowed"));
    }

    @Test
    void getGameConfiguration_shouldThrowExceptionOnQuit() {
        provideInput("4\nquit\n");
        assertThrows(InputCancelledException.class, () -> inputHandler.getGameConfiguration());
    }

    // --- Tests for promptPlayAgain ---
    @Test
    void promptPlayAgain_shouldReturnTrueForEnter() {
        provideInput("\n"); // Simulate Enter
        inputHandler = new ConsoleInputHandler(mockOutputHandler, new Scanner(System.in));
        assertTrue(inputHandler.promptPlayAgain());
    }

    @Test
    void promptPlayAgain_shouldReturnTrueForAnyNonNInput() {
        provideInput("y\n"); // Simulate 'y' + Enter
        inputHandler = new ConsoleInputHandler(mockOutputHandler, new Scanner(System.in));
        assertTrue(inputHandler.promptPlayAgain());
    }

    @Test
    void promptPlayAgain_shouldReturnFalseForN() {
        provideInput("n\n"); // Simulate 'n' + Enter
        inputHandler = new ConsoleInputHandler(mockOutputHandler, new Scanner(System.in));
        assertFalse(inputHandler.promptPlayAgain());
    }

} 