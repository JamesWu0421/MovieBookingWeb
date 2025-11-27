package com.example.movie.exception;

public class SeatAlreadyLockedException extends RuntimeException {
    public SeatAlreadyLockedException(String message) {
        super(message);
    }
}