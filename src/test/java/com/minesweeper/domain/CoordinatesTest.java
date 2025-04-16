package com.minesweeper.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Coordinates class.
 */
class CoordinatesTest {

    @Test
    void constructorAndGetters_shouldWorkCorrectly() {
        int row = 5;
        int col = 10;
        Coordinates coords = new Coordinates(row, col);

        assertEquals(row, coords.row(), "Row should match the value passed to constructor");
        assertEquals(col, coords.column(), "Column should match the value passed to constructor");
    }

    @Test
    void equals_shouldReturnTrueForSameCoordinates() {
        Coordinates coords1 = new Coordinates(3, 4);
        Coordinates coords2 = new Coordinates(3, 4);

        assertEquals(coords1, coords2, "Coordinates with the same row and column should be equal");
        assertEquals(coords1.hashCode(), coords2.hashCode(), "Hash codes should be equal for equal objects");
    }

    @Test
    void equals_shouldReturnFalseForDifferentRows() {
        Coordinates coords1 = new Coordinates(3, 4);
        Coordinates coords2 = new Coordinates(4, 4);

        assertNotEquals(coords1, coords2, "Coordinates with different rows should not be equal");
    }

    @Test
    void equals_shouldReturnFalseForDifferentColumns() {
        Coordinates coords1 = new Coordinates(3, 4);
        Coordinates coords2 = new Coordinates(3, 5);
        assertNotEquals(coords1, coords2, "Coordinates with different columns should not be equal");
    }

    @Test
    void equals_shouldReturnFalseForDifferentObjectTypes() {
        Coordinates coordinates = new Coordinates(1, 1);
        String otherObject = "1,1";
        assertNotEquals(otherObject, coordinates, "Coordinates should not be equal to an object of a different type");
    }

     @Test
    void equals_shouldReturnFalseForNull() {
        Coordinates coords = new Coordinates(1, 1);
         assertNotEquals(null, coords, "Coordinates should not be equal to null");
    }

    @Test
    void toString_shouldReturnReadableRepresentation() {
        Coordinates coordinates = new Coordinates(2, 8);
        assertTrue(coordinates.toString().contains("row=2"), "toString should include the row value");
        assertTrue(coordinates.toString().contains("column=8"), "toString should include the column value");
    }
} 