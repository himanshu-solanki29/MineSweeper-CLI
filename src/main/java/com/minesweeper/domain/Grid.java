package com.minesweeper.domain;

import java.util.ArrayList;
import java.util.List;

import com.minesweeper.handler.MinePlacer;

/**
 * Represents the Minesweeper game board, containing a 2D arrangement of Cells.
 * Manages the creation and access to cells within the grid boundaries.
 */
public class Grid {

    private final int size;
    private final Cell[][] cells;

    /**
     * Constructs a new Grid of the specified size.
     * Initializes all cells to their default state.
     *
     * @param size The width and height of the square grid.
     * @throws IllegalArgumentException if size is not positive.
     */
    public Grid(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Grid size must be positive, but was " + size);
        }
        this.size = size;
        this.cells = new Cell[size][size];
        initializeCells();
    }

    private void initializeCells() {
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                cells[r][c] = new Cell();
            }
        }
    }

    /**
     * Gets the size (width and height) of the grid.
     *
     * @return The size of the grid.
     */
    public int getSize() {
        return size;
    }

    /**
     * Retrieves the Cell at the specified coordinates.
     *
     * @param coordinates The coordinates of the cell to retrieve.
     * @return The Cell at the given coordinates.
     * @throws IndexOutOfBoundsException if the coordinates are outside the grid boundaries.
     */
    public Cell getCell(Coordinates coordinates) {
        if (!isValidCoordinate(coordinates)) {
            throw new IndexOutOfBoundsException("Coordinates out of bounds: " + coordinates + " for grid size " + size);
        }
        return cells[coordinates.row()][coordinates.column()];
    }

    /**
     * Checks if the given coordinates are within the valid bounds of the grid.
     *
     * @param coordinates The coordinates to check.
     * @return true if the coordinates are valid, false otherwise.
     */
    public boolean isValidCoordinate(Coordinates coordinates) {
        return coordinates.row() >= 0 && coordinates.row() < size &&
                coordinates.column() >= 0 && coordinates.column() < size;
    }

    /**
     * Gets a list of valid coordinates for all cells adjacent (including diagonals)
     * to the given coordinates.
     *
     * @param coordinates The coordinates of the center cell.
     * @return A List of valid Coordinates adjacent to the given cell.
     * @throws IndexOutOfBoundsException if the input coordinates are outside the grid.
     */
    public List<Coordinates> getAdjacentCoordinates(Coordinates coordinates) {
        if (!isValidCoordinate(coordinates)) {
            throw new IndexOutOfBoundsException("Cannot get neighbors for coordinates outside the grid: " + coordinates);
        }

        List<Coordinates> neighbors = new ArrayList<>();
        int r = coordinates.row();
        int c = coordinates.column();

        // Loop through the 3x3 area around the cell
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                // Skip the center cell itself
                if (dr == 0 && dc == 0) {
                    continue;
                }

                Coordinates neighborCoordinates = new Coordinates(r + dr, c + dc);

                // Check if the neighbor coordinates are within the grid bounds
                if (isValidCoordinate(neighborCoordinates)) {
                    neighbors.add(neighborCoordinates);
                }
            }
        }
        return neighbors;
    }

    /**
     * Initializes the grid by placing mines using the provided MinePlacer
     * and calculating adjacent mine counts for all cells.
     *
     * @param minePlacer The strategy for placing mines.
     * @param mineCount  The total number of mines to place.
     */
    public void initialize(MinePlacer minePlacer, int mineCount) {
        // Place mines using the provided strategy
        minePlacer.placeMines(this, mineCount);
        // Calculate adjacent mine counts
        calculateAdjacentMineCounts();
    }

    /**
     * Calculates and sets the adjacent mine count for every non-mine cell in the grid.
     * This should be called after mines have been placed.
     */
    private void calculateAdjacentMineCounts() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Coordinates currentCoordinates = new Coordinates(row, col);
                Cell currentCell = getCell(currentCoordinates);
                // Skip calculation for cells that are mines themselves
                if (currentCell.isMine()) {
                    continue;
                }
                int adjacentMines = 0;
                List<Coordinates> neighbors = getAdjacentCoordinates(currentCoordinates);
                for (Coordinates neighborCoordinates : neighbors) {
                    // No need to check bounds here, getAdjacentCoordinates handles it
                    if (getCell(neighborCoordinates).isMine()) {
                        adjacentMines++;
                    }
                }
                currentCell.setAdjacentMineCount(adjacentMines);
            }
        }
    }

    /**
     * Reveals the cell at the specified coordinates.
     * If the revealed cell has zero adjacent mines, it triggers a cascade reveal
     * of adjacent non-mine, hidden cells.
     *
     * @param coordinates The coordinates of the cell to reveal.
     * @throws IndexOutOfBoundsException if the coordinates are outside the grid.
     */
    public void revealCell(Coordinates coordinates) {
        // getCell handles the bounds check
        Cell cell = getCell(coordinates);

        // Do nothing if the cell is already revealed
        if (cell.isRevealed()) {
            return;
        }

        cell.reveal();

        if (cell.getAdjacentMineCount() == 0) {
            revealAdjacentCells(coordinates);
        }

    }

    /**
     * Recursively reveals adjacent cells that have no adjacent mines.
     * 
     * @param center The coordinates of the cell to start revealing from.
     */
    private void revealAdjacentCells(Coordinates center) {
        int[] directions = {-1, 0, 1};
        for (int rowOffset : directions) {
            for (int colOffset : directions) {
                // Skip the center cell itself
                if (rowOffset == 0 && colOffset == 0) {
                    continue;
                }
                Coordinates neighborCoordinates = new Coordinates(
                        center.row() + rowOffset,
                        center.column() + colOffset
                );
                if (isValidCoordinate(neighborCoordinates)) {
                    revealCell(neighborCoordinates);
                }
            }
        }
    }


    /**
     * Checks if all cells that do not contain mines have been revealed.
     *
     * @return true if all non-mine cells are revealed, false otherwise.
     */
    public boolean areAllNonMinesRevealed() {
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                Cell cell = cells[r][c];
                // If we find a cell that is NOT a mine AND is NOT revealed, the condition is false
                if (!cell.isMine() && !cell.isRevealed()) {
                    return false;
                }
            }
        }
        // If the loop completes without finding any unrevealed non-mine cells, the condition is true
        return true;
    }

    /**
     * Helper method to reveal all mine locations, typically used at game over.
     */
    public void revealAllMines() { // Changed to public for potential use in testing/display
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                Cell cell = cells[r][c];
                if (cell.isMine()) {
                    cell.reveal();
                }
            }
        }
    }
} 