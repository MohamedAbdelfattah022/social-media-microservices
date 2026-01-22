package com.socialmedia.feedservice.exception;


public abstract sealed class FeedServiceException extends RuntimeException
        permits ServiceUnavailableException, InvalidRequestException {

    protected FeedServiceException(String message) {
        super(message);
    }

    protected FeedServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
