package com.socialmedia.feedservice.exception;

public final class ServiceUnavailableException extends FeedServiceException {
    
    public ServiceUnavailableException(String serviceName, Throwable cause) {
        super("Service unavailable: " + serviceName, cause);
    }
    
    public ServiceUnavailableException(String serviceName) {
        super("Service unavailable: " + serviceName);
    }
}
