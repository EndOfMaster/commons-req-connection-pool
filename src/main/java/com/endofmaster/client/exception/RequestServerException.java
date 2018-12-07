package com.endofmaster.client.exception;

/**
 * @author ZM.Wang
 */
public class RequestServerException extends RuntimeException {

    public RequestServerException() {
        this("Request Server Error");
    }

    public RequestServerException(String message) {
        super(message);
    }

    public RequestServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
