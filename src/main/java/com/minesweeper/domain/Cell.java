package com.minesweeper.domain;

/**
 * Represents a single cell on the Minesweeper grid.
 * It holds information about whether it contains a mine,
 * its revealed state, and the count of adjacent mines.
 */
public class Cell {

    private boolean isMine;
    private boolean isRevealed;
    private int adjacentMineCount;

    /**
     * Constructs a new Cell.
     * By default, a cell is hidden, does not contain a mine,
     * and has an adjacent mine count of 0.
     */
    public Cell() {
        this.isMine = false;
        this.isRevealed = false;
        this.adjacentMineCount = 0; // Default value
    }

    /**
     * Checks if this cell contains a mine.
     *
     * @return true if the cell contains a mine, false otherwise.
     */
    public boolean isMine() {
        return isMine;
    }

    /**
     * Checks if this cell has been revealed by the player.
     *
     * @return true if the cell is revealed, false otherwise.
     */
    public boolean isRevealed() {
        return isRevealed;
    }

    /**
     * Gets the number of adjacent cells that contain mines.
     * This value is typically calculated and set by the Grid.
     *
     * @return The count of adjacent mines.
     */
    public int getAdjacentMineCount() {
        return adjacentMineCount;
    }

    /**
     * Sets the count of adjacent mines for this cell.
     *
     * @param adjacentMineCount The number of adjacent mines.
     */
    public void setAdjacentMineCount(int adjacentMineCount) {
        this.adjacentMineCount = adjacentMineCount;
    }

    /**
     * Places a mine in this cell. Should typically only be called during grid setup.
     */
    public void placeMine() {
        this.isMine = true;
    }

    /**
     * Marks this cell as revealed. This action is usually triggered by the player.
     */
    public void reveal() {
        this.isRevealed = true;
    }

} 