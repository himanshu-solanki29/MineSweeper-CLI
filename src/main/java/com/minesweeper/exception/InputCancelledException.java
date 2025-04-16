package com.minesweeper.exception;

/**
 * Custom exception indicating that the user intentionally cancelled an input operation
 * (e.g., by typing 'quit').
 */
public class InputCancelledException extends Exception {
    public InputCancelledException(String message) {
        super(message);
    }
} 