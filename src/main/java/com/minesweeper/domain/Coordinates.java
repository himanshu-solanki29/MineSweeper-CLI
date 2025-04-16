package com.minesweeper.domain;

/**
 * Represents an immutable pair of row and column coordinates on the Minesweeper grid.
 * Implemented as a final class with explicit methods.
 */
public record Coordinates(int row, int column) {
} 