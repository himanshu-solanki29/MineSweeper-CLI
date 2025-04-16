package com.minesweeper.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Cell class.
 */
class CellTest {

    private Cell cell;

    @BeforeEach
    void setUp() {
        // Create a new Cell before each test
        cell = new Cell();
    }

    @Test
    void constructor_shouldInitializeCellAsHiddenAndNotMine() {
        assertFalse(cell.isMine(), "Newly created cell should not be a mine by default");
        assertFalse(cell.isRevealed(), "Newly created cell should be hidden by default");
        assertEquals(0, cell.getAdjacentMineCount(), "Newly created cell should have 0 adjacent mines initially"); // Assuming default is 0, could also be -1
    }

    @Test
    void placeMine_shouldMarkCellAsMine() {
        cell.placeMine();
        assertTrue(cell.isMine(), "Cell should be marked as a mine after placeMine()");
    }

    @Test
    void reveal_shouldMarkCellAsRevealed() {
        assertFalse(cell.isRevealed(), "Cell should be hidden initially");
        cell.reveal();
        assertTrue(cell.isRevealed(), "Cell should be revealed after reveal()");
    }

    @Test
    void setAndGetAdjacentMineCount_shouldWorkCorrectly() {
        int count = 3;
        cell.setAdjacentMineCount(count);
        assertEquals(count, cell.getAdjacentMineCount(), "Adjacent mine count should match the value set");
    }

    @Test
    void setAdjacentMineCount_shouldAllowChangingValue() {
        cell.setAdjacentMineCount(2);
        assertEquals(2, cell.getAdjacentMineCount(), "Initial adjacent mine count should be 2");
        cell.setAdjacentMineCount(5);
        assertEquals(5, cell.getAdjacentMineCount(), "Adjacent mine count should be updated to 5");
    }
} 