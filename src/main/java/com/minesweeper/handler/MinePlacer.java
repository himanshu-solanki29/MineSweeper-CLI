package com.minesweeper.handler;

import com.minesweeper.domain.Grid;

/**
 * Interface defining the contract for strategies that place mines on a Grid.
 */
@FunctionalInterface
public interface MinePlacer {

    /**
     * Places the required number of mines onto the given grid according to
     * the specific placement strategy.
     *
     * @param grid The Grid object to place mines on.
     * @param mineCount The total number of mines to place.
     */
    void placeMines(Grid grid, int mineCount);

} 