package com.minesweeper.config;

/**
 * An immutable value object holding the configuration settings for a Minesweeper game instance.
 *
 * @param gridSize The size (width and height) of the square grid.
 * @param mineCount The number of mines to be placed on the grid.
 */
public record GameConfiguration(int gridSize, int mineCount) {
} 