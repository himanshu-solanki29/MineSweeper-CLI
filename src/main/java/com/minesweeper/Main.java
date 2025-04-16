package com.minesweeper;

import com.minesweeper.game.MinesweeperGame;
import com.minesweeper.handler.InputHandler;
import com.minesweeper.handler.MinePlacer;
import com.minesweeper.handler.OutputHandler;
import com.minesweeper.io.console.ConsoleInputHandler;
import com.minesweeper.io.console.ConsoleOutputHandler;
import com.minesweeper.placement.RandomMinePlacer;

/**
 * Main application entry point for the Minesweeper game.
 * Creates the necessary components and starts the game.
 */
public class Main {

    public static void main(String[] args) {
        // Instantiate concrete implementations
        OutputHandler outputHandler = new ConsoleOutputHandler();
        // ConsoleInputHandler needs the OutputHandler for printing prompts/errors
        InputHandler inputHandler = new ConsoleInputHandler(outputHandler);
        MinePlacer minePlacer = new RandomMinePlacer(); // Use random placement
        // Inject dependencies into the game orchestrator
        MinesweeperGame game = new MinesweeperGame(inputHandler, outputHandler, minePlacer);
        // Run the game
        game.run();
    }
} 