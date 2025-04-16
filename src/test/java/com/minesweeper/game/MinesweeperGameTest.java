package com.minesweeper.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.minesweeper.config.GameConfiguration;
import com.minesweeper.domain.Coordinates;
import com.minesweeper.domain.Grid;
import com.minesweeper.exception.InputCancelledException;
import com.minesweeper.handler.InputHandler;
import com.minesweeper.handler.MinePlacer;
import com.minesweeper.handler.OutputHandler;

/**
 * Unit tests for the MinesweeperGame orchestrator class.
 * Mocks dependencies to test game flow logic.
 * Test controlled inputs from its collaborators.
 */
class MinesweeperGameTest {


    private MockInputHandler mockInputHandler;
    private MockOutputHandler mockOutputHandler;
    private MockMinePlacer mockMinePlacer;

    private MinesweeperGame game; // Instance under test

    // Default configuration for tests
    private final GameConfiguration testConfig = new GameConfiguration(4, 3);

    @BeforeEach
    void setUp() {
        mockInputHandler = new MockInputHandler();
        mockOutputHandler = new MockOutputHandler();
        mockMinePlacer = new MockMinePlacer();
        game = new MinesweeperGame(mockInputHandler, mockOutputHandler, mockMinePlacer);
    }

    @Test
    void run_shouldDisplayWelcomeMessageFirst() {
        mockInputHandler.setConfigToReturn(testConfig);
        mockInputHandler.setMovesToReturn();
        mockInputHandler.setPlayAgain(false);
        game.run();
        assertTrue(mockOutputHandler.displayWelcomeCalled);
    }

    @Test
    void run_shouldGetGameConfiguration() {
        mockInputHandler.setConfigToReturn(testConfig);
        mockInputHandler.setMovesToReturn();
        mockInputHandler.setPlayAgain(false);
        game.run();
        assertTrue(mockInputHandler.getGameConfigurationCalled);
    }

    @Test
    void run_shouldInitializeGridUsingMinePlacerAndConfig() {
        mockInputHandler.setConfigToReturn(testConfig);
        mockInputHandler.setMovesToReturn();
        mockInputHandler.setPlayAgain(false);
        game.run();
        assertTrue(mockMinePlacer.placeMinesCalled);
        assertNotNull(mockMinePlacer.gridPassedToPlaceMines);
        assertEquals(testConfig.gridSize(), mockMinePlacer.gridPassedToPlaceMines.getSize());
        assertEquals(testConfig.mineCount(), mockMinePlacer.mineCountPassedToPlaceMines);
    }

    // --- Tests for game loop ---
    @Test
    void run_shouldPlayGameUntilWin()  {
        GameConfiguration winConfig = new GameConfiguration(2, 1);
        Coordinates minePos = new Coordinates(0, 0);
        Coordinates safePos1 = new Coordinates(0, 1);
        Coordinates safePos2 = new Coordinates(1, 0);
        Coordinates safePos3 = new Coordinates(1, 1);

        mockInputHandler.setConfigToReturn(winConfig);
        mockMinePlacer.setMinesToPlace(minePos);
        mockInputHandler.setMovesToReturn(safePos1, safePos2, safePos3);
        mockInputHandler.setPlayAgain(false);

        game.run();

        assertTrue(mockOutputHandler.displayWelcomeCalled);
        assertTrue(mockInputHandler.getGameConfigurationCalled);
        assertTrue(mockMinePlacer.placeMinesCalled);
        assertEquals(3, mockInputHandler.moveIndex);
        assertTrue(mockOutputHandler.displayGridCalled);
        assertTrue(mockOutputHandler.displayAdjCountCalled);
        assertTrue(mockOutputHandler.displayWinCalled);
        assertFalse(mockOutputHandler.displayMineHitCalled);
        assertTrue(mockInputHandler.promptPlayAgainCalled);
        assertTrue(mockOutputHandler.displayGoodbyeCalled);
    }

    @Test
    void run_shouldPlayGameUntilLoss()  {
        GameConfiguration loseConfig = new GameConfiguration(2, 1);
        Coordinates minePos = new Coordinates(0, 0);

        mockInputHandler.setConfigToReturn(loseConfig);
        mockMinePlacer.setMinesToPlace(minePos);
        mockInputHandler.setMovesToReturn(minePos);
        mockInputHandler.setPlayAgain(false);

        game.run();

        assertTrue(mockOutputHandler.displayWelcomeCalled);
        assertTrue(mockInputHandler.getGameConfigurationCalled);
        assertTrue(mockMinePlacer.placeMinesCalled);
        assertEquals(1, mockInputHandler.moveIndex);
        assertTrue(mockOutputHandler.displayGridCalled);
        assertTrue(mockOutputHandler.displayMineHitCalled);
        assertFalse(mockOutputHandler.displayWinCalled);
        assertFalse(mockOutputHandler.displayAdjCountCalled);
        assertTrue(mockInputHandler.promptPlayAgainCalled);
        assertTrue(mockOutputHandler.displayGoodbyeCalled);
    }


    // --- Mock Implementations ---
    private static class MockInputHandler implements InputHandler {
        boolean getGameConfigurationCalled = false;
        boolean getMoveInputCalled = false;
        boolean promptPlayAgainCalled = false;
        GameConfiguration configToReturn = null;
        Coordinates[] movesToReturn = {};
        int moveIndex = 0;
        boolean playAgain = false;
        InputCancelledException cancelException = null;

        void setConfigToReturn(GameConfiguration config) {
            this.configToReturn = config;
        }

        void setMovesToReturn(Coordinates... moves) {
            this.movesToReturn = moves;
            this.moveIndex = 0;
        }

        void setPlayAgain(boolean value) {
            this.playAgain = value;
        }

        @Override
        public GameConfiguration getGameConfiguration() throws InputCancelledException {
            getGameConfigurationCalled = true;
            if (cancelException != null) throw cancelException;
            if (configToReturn == null) throw new IllegalStateException("Test setup error: configToReturn not set");
            return configToReturn;
        }

        @Override
        public Coordinates getMoveInput(int gridSize) throws InputCancelledException {
            getMoveInputCalled = true;
            if (cancelException != null) throw cancelException;
            if (moveIndex >= movesToReturn.length) {
                throw new InputCancelledException("Simulated quit - no more moves");
            }
            return movesToReturn[moveIndex++];
        }

        @Override
        public boolean promptPlayAgain() {
            promptPlayAgainCalled = true;
            return playAgain;
        }
    }

    private static class MockOutputHandler implements OutputHandler {
        boolean displayWelcomeCalled = false;
        boolean displayGridCalled = false;
        boolean displayPromptForMoveCalled = false;
        boolean displayMineHitCalled = false;
        boolean displayWinCalled = false;
        boolean displayAdjCountCalled = false;
        boolean displayInvalidInputCalled = false;
        boolean displayAlreadyRevealed = false;
        boolean displayPlayAgainCalled = false;
        boolean displayGoodbyeCalled = false;
        Grid lastGridDisplayed = null;
        int lastAdjCount = -1;

        @Override
        public void displayWelcomeMessage() {
            displayWelcomeCalled = true;
        }

        @Override
        public void displayGrid(Grid grid) {
            displayGridCalled = true;
            lastGridDisplayed = grid;
        }

        @Override
        public void displayPromptForMove() {
            displayPromptForMoveCalled = true;
        }

        @Override
        public void displayMineHitMessage() {
            displayMineHitCalled = true;
        }

        @Override
        public void displayWinMessage() {
            displayWinCalled = true;
        }

        @Override
        public void displayAdjacentMineCount(int count) {
            displayAdjCountCalled = true;
            lastAdjCount = count;
        }

        @Override
        public void displayInvalidInputMessage(String input, String reason) {
            displayInvalidInputCalled = true;
        }

        @Override
        public void displayAlreadyRevealedMessage() { displayAlreadyRevealed = true; }

        @Override
        public void displayPromptPlayAgain() {
            displayPlayAgainCalled = true;
        }

        @Override
        public void displayGoodbyeMessage() {
            displayGoodbyeCalled = true;
        }
    }

    private static class MockMinePlacer implements MinePlacer {
        boolean placeMinesCalled = false;
        Grid gridPassedToPlaceMines = null;
        int mineCountPassedToPlaceMines = -1;
        Coordinates[] minesToPlace = {};

        void setMinesToPlace(Coordinates... coordinates) {
            this.minesToPlace = coordinates;
        }

        @Override
        public void placeMines(Grid grid, int mineCount) {
            placeMinesCalled = true;
            gridPassedToPlaceMines = grid;
            mineCountPassedToPlaceMines = mineCount;
            if (minesToPlace != null) {
                for (Coordinates coordinates : minesToPlace) {
                    if (grid.isValidCoordinate(coordinates)) {
                        grid.getCell(coordinates).placeMine();
                    }
                }
            }
        }
    }


} 