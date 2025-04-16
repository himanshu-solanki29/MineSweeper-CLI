# Minesweeper

A command-line implementation of the classic Minesweeper game, written in Java.

## Description

This project is a console-based Minesweeper game where the player attempts to clear a square grid containing hidden mines without detonating any of them. The game follows the standard Minesweeper rules, including revealing adjacent mine counts and automatically clearing safe areas.

## Features

*   **Command-Line Interface:** Play the game entirely through text commands in your console.
*   **Configurable Grid:** Set the grid size (square) and the number of mines at the start of each game.
*   **Mine Detection:** Uncover squares to reveal the number of adjacent mines.
*   **Auto-Reveal:** Squares with zero adjacent mines automatically reveal their neighboring squares.
*   **Win/Loss Conditions:** The game ends when a mine is revealed (loss) or all non-mine squares are uncovered (win).
*   **Play Again:** Option to start a new game after finishing one.

## Design and Assumptions

### Design Philosophy

The application is built using Java 17 and Maven, adhering to Object-Oriented principles and aiming for clean, maintainable, and testable code following SOLID guidelines:

*   **Modularity & Separation of Concerns:** The codebase is divided into distinct packages (`domain`, `game`, `io`, `handler`, `config`, `placement`, `exception`). This isolates core game logic from infrastructure concerns like input/output or specific algorithms like mine placement.
*   **Dependency Inversion:** Key components interact through interfaces (`InputHandler`, `OutputHandler`, `MinePlacer`). The main game orchestrator (`MinesweeperGame`) depends on these abstractions, not concrete implementations.
*   **Dependency Injection:** Concrete implementations (like `ConsoleInputHandler`, `ConsoleOutputHandler`, `RandomMinePlacer`) are instantiated in the `Main` class and passed into the `MinesweeperGame`, making it easy to swap implementations.
*   **Testability:** The use of interfaces and dependency injection allows the core game logic to be unit-tested in isolation using JUnit 5. Mock implementations are used to simulate dependencies during tests.
*   **Immutability:** Where practical (e.g., `Coordinates`, `GameConfiguration`), objects are immutable to improve predictability and thread safety (though the current application is single-threaded).

### Development Approach & Testing

*   **Test-Driven Development (TDD):** The project was developed following a TDD approach where possible. Tests were written before or concurrently with the application code to guide the design and ensure correctness.
*   **Testability:** The use of interfaces and dependency injection is key to enabling isolated unit testing. The core game logic (`MinesweeperGame`, `Grid`, domain classes) is thoroughly unit-tested using JUnit 5. Mock implementations are used to simulate dependencies during tests.
*   **Test Coverage:** The project aims for high test coverage. Most core logic classes achieve 100% coverage, and overall class coverage exceeds 90%, ensuring robustness and confidence in the implementation.

### Assumptions


*   **Square Grid:** The game currently assumes and enforces a square grid (NxN) as per the initial prompt interpretation ("Enter the size of the grid (e.g., 4 for a 4x4 grid)"). Extending to rectangular grids would require minor modifications, primarily in input handling and potentially grid representation.
    * We could make an interface called Grid which would be implemented by concrete classes like SquareGrid, RectangularGrid etc.    
* **Console Interface:** The primary interface is the command line. While the design supports adding other interfaces (GUI, web), only the console is implemented.
*   **Valid Input Ranges:** The input handling expects reasonable integer inputs for grid size and mine count. While basic validation (like max mine percentage) is included, it assumes users won't intentionally provide extremely large or negative numbers beyond simple checks.
*   **Randomness:** The default mine placement uses `java.util.Random`. It's assumed this provides sufficient randomness for a playable game experience.
*   **Single Player:** The game is designed for a single player interacting sequentially via the console.
*   **Quitting the Game:** Games quits gracefully in case of input exceptions, also added option to quit game.  

## Requirements

*   **Java Development Kit (JDK):** Version 17 or later.
*   **Apache Maven:** Version 3.x or later (for building and running).

## Building the Project

1.  Unzip the source code.
2.  Navigate to the project's root directory (where `pom.xml` is located) in your terminal.
3.  Run the following Maven command to compile the code and package it into an executable JAR:
    ```bash
    mvn clean package
    ```
    This will create a JAR file in the `target/` directory (e.g., `target/minesweeper-1.0.0.jar`).

## Running the Application

1.  Make sure you have built the project (see above).
2.  Navigate to the project's root directory.
3.  Run the game using the following command:
    ```bash
    java -jar target/minesweeper-1.0.0.jar
    ```
    *(Replace `minesweeper-1.0.0.jar` with the actual name of the generated JAR file if it differs)*.
4.  Follow the on-screen prompts to play the game.

## Running Tests

To execute the unit tests, run the following Maven command from the project root directory:

```bash
mvn test
```

## Project Structure
```
minesweeper/
├── pom.xml # Maven build configuration
├── src/
│ ├── main/
│ │ └── java/
│ │ └── com/
│ │ └── minesweeper/ # Root package
│ │ ├── Main.java # Application entry point
│ │ ├── config/ # Game configuration classes
│ │ ├── domain/ # Core domain objects (Grid, Cell, etc.)
│ │ ├── exception/ # Custom exceptions
│ │ ├── game/ # Main game orchestration logic
│ │ ├── handler/ # Interfaces for I/O and placement
│ │ ├── io/ # Concrete I/O implementations (console)
│ │ └── placement/ # Concrete mine placement strategies
│ └── test/
│ └── java/
│ └── com/
│ └── minesweeper/ # Unit tests mirroring main structure
└── target/ # Compiled code and JAR file (after build)
```

## Author
[Himanshu Solanki](https://github.com/himanshu-solanki29)




