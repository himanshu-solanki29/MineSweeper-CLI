package com.minesweeper.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

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

        assertTrue(coords1.equals(coords2), "Coordinates with the same row and column should be equal");
        assertEquals(coords1.hashCode(), coords2.hashCode(), "Hash codes should be equal for equal objects");
    }

    @Test
    void equals_shouldReturnFalseForDifferentRows() {
        Coordinates coords1 = new Coordinates(3, 4);
        Coordinates coords2 = new Coordinates(4, 4);

        assertFalse(coords1.equals(coords2), "Coordinates with different rows should not be equal");
        // Note: Hash codes *might* collide, but it's unlikely for simple cases and not strictly required by equals contract
        // assertNotEquals(coords1.hashCode(), coords2.hashCode());
    }

    @Test
    void equals_shouldReturnFalseForDifferentColumns() {
        Coordinates coords1 = new Coordinates(3, 4);
        Coordinates coords2 = new Coordinates(3, 5);

        assertFalse(coords1.equals(coords2), "Coordinates with different columns should not be equal");
    }

    @Test
    void equals_shouldReturnFalseForDifferentObjectTypes() {
        Coordinates coords = new Coordinates(1, 1);
        String otherObject = "1,1";

        assertFalse(coords.equals(otherObject), "Coordinates should not be equal to an object of a different type");
    }

     @Test
    void equals_shouldReturnFalseForNull() {
        Coordinates coords = new Coordinates(1, 1);
        assertFalse(coords.equals(null), "Coordinates should not be equal to null");
    }

    @Test
    void toString_shouldReturnReadableRepresentation() {
        Coordinates coords = new Coordinates(2, 8);
        // Example format, specific format isn't strictly required by tests but good for debugging
        String expectedString = "Coordinates[row=2, column=8]"; 
        // If using a record, the default toString is usually fine and matches this format.
        // If using a class, you might need to implement toString().
        
        // Let's just check it contains row and column values for now
        assertTrue(coords.toString().contains("row=2"), "toString should include the row value");
        assertTrue(coords.toString().contains("column=8"), "toString should include the column value");
    }
} 