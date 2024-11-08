package com.bitescout.app.reservationservice.exception;

public class InvalidStatusRequestException extends RuntimeException {
    public InvalidStatusRequestException(String message) {
        super(message);
    }
}
