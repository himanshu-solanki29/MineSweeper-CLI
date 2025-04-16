package com.minesweeper.placement;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import com.minesweeper.domain.Coordinates;
import com.minesweeper.domain.Grid;

/**
 * Unit tests for the RandomMinePlacer class.
 */
class RandomMinePlacerTest {

    @Test
    void placeMines_shouldPlaceCorrectNumberOfMines() {
        int size = 10;
        int mineCount = 15;
        Grid grid = new Grid(size);
        Random predictableRandom = new Random(12345L);
        // Use concrete type
        RandomMinePlacer placer = new RandomMinePlacer(predictableRandom);

        placer.placeMines(grid, mineCount);
        int actualMines = countMines(grid);
        assertEquals(mineCount, actualMines);
    }

    // Repeat the test a few times with different seeds (or default random) 
    // to increase confidence it works generally, though true randomness is hard to assert.
    @RepeatedTest(5)
    void placeMines_repeated_shouldPlaceCorrectNumberOfMines() {
        int size = 8;
        int mineCount = 10;
        Grid grid = new Grid(size);
        // Use concrete type
        RandomMinePlacer placer = new RandomMinePlacer(); 
        placer.placeMines(grid, mineCount);
        int actualMines = countMines(grid);
        assertEquals(mineCount, actualMines);
    }

    @Test
    void placeMines_shouldHandleZeroMines() {
        int size = 5;
        int mineCount = 0;
        Grid grid = new Grid(size);
        RandomMinePlacer placer = new RandomMinePlacer();
        placer.placeMines(grid, mineCount);
        int actualMines = countMines(grid);
        assertEquals(mineCount, actualMines);
    }

    @Test
    void placeMines_shouldHandlePlacingAllMines() {
        int size = 3;
        int mineCount = size * size;
        Grid grid = new Grid(size);
        RandomMinePlacer placer = new RandomMinePlacer();
        placer.placeMines(grid, mineCount);
        int actualMines = countMines(grid);
        assertEquals(mineCount, actualMines);
    }

    @Test
    void placeMines_on1x1Grid_placeOneMine() {
        int size = 1;
        int mineCount = 1;
        Grid grid = new Grid(size);
        RandomMinePlacer placer = new RandomMinePlacer();
        placer.placeMines(grid, mineCount);
        int actualMines = countMines(grid);
        assertEquals(mineCount, actualMines);
        assertTrue(grid.getCell(new Coordinates(0,0)).isMine());
    }

    @Test
    void placeMines_on1x1Grid_placeZeroMines() {
        int size = 1;
        int mineCount = 0;
        Grid grid = new Grid(size);
        RandomMinePlacer placer = new RandomMinePlacer();
        placer.placeMines(grid, mineCount);
        int actualMines = countMines(grid);
        assertEquals(mineCount, actualMines);
         assertFalse(grid.getCell(new Coordinates(0,0)).isMine());
    }

     @Test
    void placeMines_shouldThrowExceptionIfMineCountExceedsGridSize() {
        int size = 4;
        int mineCount = size * size + 1; 
        Grid grid = new Grid(size);
        RandomMinePlacer placer = new RandomMinePlacer();
        assertThrows(IllegalArgumentException.class, () -> placer.placeMines(grid, mineCount));
    }
    
    @Test
    void placeMines_shouldThrowExceptionForNegativeMineCount() {
        int size = 4;
        int mineCount = -1;
        Grid grid = new Grid(size);
        RandomMinePlacer placer = new RandomMinePlacer();
        assertThrows(IllegalArgumentException.class, () -> placer.placeMines(grid, mineCount));
    }

    // Helper method to count mines in a grid
    private int countMines(Grid grid) {
        int count = 0;
        int size = grid.getSize();
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (grid.getCell(new Coordinates(r, c)).isMine()) {
                    count++;
                }
            }
        }
        return count;
    }
} 