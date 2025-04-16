package com.minesweeper.domain;

/**
 * Represents an immutable pair of row and column coordinates on the Minesweeper grid.
 * 
 * @param row The row index of the cell.
 * @param column The column index of the cell.
 */
public record Coordinates(int row, int column) {
} 