package com.bitescout.app.reviewservice.review.exception;

public class ReviewMissingFieldException extends RuntimeException {
    public ReviewMissingFieldException(String message) {
        super(message);
    }
}
