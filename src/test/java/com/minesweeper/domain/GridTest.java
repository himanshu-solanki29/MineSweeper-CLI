package com.minesweeper.domain;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.minesweeper.handler.MinePlacer;

/**
 * Unit tests for the Grid class.
 */
class GridTest {
    private Grid grid;
    private static final int GRID_SIZE = 5;

    @BeforeEach
    void setUp() {
        grid = new Grid(GRID_SIZE);
    }

    @Test
    void constructor_WithValidSize_CreatesGrid() {
        assertEquals(GRID_SIZE, grid.getSize());
    }

    @Test
    void constructor_WithInvalidSize_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Grid(0));
        assertThrows(IllegalArgumentException.class, () -> new Grid(-1));
    }

    @Test
    void getCell_WithValidCoordinates_ReturnsCell() {
        Coordinates coordinates = new Coordinates(0, 0);
        assertNotNull(grid.getCell(coordinates));
    }

    @Test
    void getCell_WithInvalidCoordinates_ThrowsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> grid.getCell(new Coordinates(-1, 0)));
        assertThrows(IndexOutOfBoundsException.class, () -> grid.getCell(new Coordinates(GRID_SIZE, 0)));
        assertThrows(IndexOutOfBoundsException.class, () -> grid.getCell(new Coordinates(0, -1)));
        assertThrows(IndexOutOfBoundsException.class, () -> grid.getCell(new Coordinates(0, GRID_SIZE)));
    }

    @Test
    void isValidCoordinate_WithValidCoordinates_ReturnsTrue() {
        assertTrue(grid.isValidCoordinate(new Coordinates(0, 0)));
        assertTrue(grid.isValidCoordinate(new Coordinates(GRID_SIZE - 1, GRID_SIZE - 1)));
    }

    @Test
    void isValidCoordinate_WithInvalidCoordinates_ReturnsFalse() {
        assertFalse(grid.isValidCoordinate(new Coordinates(-1, 0)));
        assertFalse(grid.isValidCoordinate(new Coordinates(0, -1)));
        assertFalse(grid.isValidCoordinate(new Coordinates(GRID_SIZE, 0)));
        assertFalse(grid.isValidCoordinate(new Coordinates(0, GRID_SIZE)));
    }

    @Test
    void getAdjacentCoordinates_WithCenterCell_ReturnsAllAdjacentCells() {
        Coordinates center = new Coordinates(2, 2);
        List<Coordinates> adjacent = grid.getAdjacentCoordinates(center);
        assertEquals(8, adjacent.size());
    }

    @Test
    void getAdjacentCoordinates_WithCornerCell_ReturnsValidAdjacentCells() {
        Coordinates corner = new Coordinates(0, 0);
        List<Coordinates> adjacent = grid.getAdjacentCoordinates(corner);
        assertEquals(3, adjacent.size());
    }

    @Test
    void getAdjacentCoordinates_WithEdgeCell_ReturnsValidAdjacentCells() {
        Coordinates edge = new Coordinates(0, 2);
        List<Coordinates> adjacent = grid.getAdjacentCoordinates(edge);
        assertEquals(5, adjacent.size());
    }

    @Test
    void getAdjacentCoordinates_WithInvalidCoordinates_ThrowsException() {
        assertThrows(IndexOutOfBoundsException.class, 
            () -> grid.getAdjacentCoordinates(new Coordinates(-1, 0)));
    }

    @Test
    void initialize_WithMinePlacer_PlacesMinesAndCalculatesCounts() {
        // Create a mock MinePlacer that places mines in specific locations
        MinePlacer mockPlacer = (grid, mineCount) -> {
            grid.getCell(new Coordinates(0, 0)).placeMine();
            grid.getCell(new Coordinates(1, 1)).placeMine();
        };

        grid.initialize(mockPlacer, 2);

        // Verify mines are placed
        assertTrue(grid.getCell(new Coordinates(0, 0)).isMine());
        assertTrue(grid.getCell(new Coordinates(1, 1)).isMine());

        // Verify adjacent mine counts
        assertEquals(2, grid.getCell(new Coordinates(0, 1)).getAdjacentMineCount());
        assertEquals(2, grid.getCell(new Coordinates(1, 0)).getAdjacentMineCount());
    }

    @Test
    void revealCell_WithNonMineCell_RevealsCell() {
        Coordinates coordinates = new Coordinates(0, 0);
        grid.revealCell(coordinates);
        assertTrue(grid.getCell(coordinates).isRevealed());
    }

    @Test
    void revealCell_WithZeroAdjacentMines_TriggersCascade() {
        // Set up a cell with zero adjacent mines
        Coordinates center = new Coordinates(2, 2);
        grid.revealCell(center);
        
        // Verify that adjacent cells are also revealed
        for (Coordinates adjacent : grid.getAdjacentCoordinates(center)) {
            assertTrue(grid.getCell(adjacent).isRevealed());
        }
    }

    @Test
    void areAllNonMinesRevealed_WithAllNonMinesRevealed_ReturnsTrue() {
        // Reveal all non-mine cells
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                Coordinates coord = new Coordinates(r, c);
                if (!grid.getCell(coord).isMine()) {
                    grid.revealCell(coord);
                }
            }
        }
        assertTrue(grid.areAllNonMinesRevealed());
    }

    @Test
    void areAllNonMinesRevealed_WithUnrevealedNonMines_ReturnsFalse() {
        assertFalse(grid.areAllNonMinesRevealed());
    }

    @Test
    void revealAllMines_RevealsAllMineCells() {
        // Place some mines
        grid.getCell(new Coordinates(0, 0)).placeMine();
        grid.getCell(new Coordinates(1, 1)).placeMine();

        grid.revealAllMines();

        assertTrue(grid.getCell(new Coordinates(0, 0)).isRevealed());
        assertTrue(grid.getCell(new Coordinates(1, 1)).isRevealed());
    }
} 