package com.endofmaster.client.exception;

/**
 * @author ZM.Wang
 */
public class RequestClientException extends RuntimeException {

    public RequestClientException() {
        this("Request Client Error");
    }

    public RequestClientException(String message) {
        super(message);
    }

    public RequestClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
