package com.minesweeper.placement;

// Import necessary classes from new locations
import java.util.Random;

import com.minesweeper.domain.Cell;
import com.minesweeper.domain.Coordinates;
import com.minesweeper.domain.Grid;
import com.minesweeper.handler.MinePlacer;

/**
 * Implements the MinePlacer interface using random placement.
 * Ensures that the specified number of mines are placed in unique, random cells.
 */
public class RandomMinePlacer implements MinePlacer {

    private final Random random;

    /**
     * Constructs a RandomMinePlacer using a default Random instance.
     */
    public RandomMinePlacer() {
        this(new Random());
    }

    /**
     * Constructs a RandomMinePlacer using the provided Random instance.
     * Useful for testing with predictable random sequences.
     *
     * @param random The Random instance to use for generating coordinates.
     */
    public RandomMinePlacer(Random random) {
        this.random = random;
    }

    /**
     * Places the specified number of mines randomly onto the grid.
     * It ensures mines are placed in unique locations.
     *
     * @param grid The Grid object to place mines on.
     * @param mineCount The total number of mines to place.
     * @throws IllegalArgumentException if mineCount is negative or exceeds the total number of cells in the grid.
     */
    @Override
    public void placeMines(Grid grid, int mineCount) {
        int size = grid.getSize();
        int totalCells = size * size;

        if (mineCount < 0) {
            throw new IllegalArgumentException("Mine count cannot be negative: " + mineCount);
        }
        if (mineCount > totalCells) {
            throw new IllegalArgumentException("Mine count (" + mineCount +
                    ") cannot exceed the total number of cells (" + totalCells + ")");
        }

        int minesPlaced = 0;
        while (minesPlaced < mineCount) {
            int r = random.nextInt(size);
            int c = random.nextInt(size);
            Coordinates coords = new Coordinates(r, c);
            Cell cell = grid.getCell(coords); // getCell performs bounds check implicitly

            // Only place a mine if the cell doesn't already have one
            if (!cell.isMine()) {
                cell.placeMine();
                minesPlaced++;
            }
            // If the cell already has a mine, the loop continues to find another random spot.
        }
    }
} 