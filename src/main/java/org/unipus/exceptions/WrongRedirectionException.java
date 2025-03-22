package org.unipus.exceptions;

public class WrongRedirectionException extends UnipusHelperException{
    public WrongRedirectionException() {
        super();
    }

    public WrongRedirectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongRedirectionException(String message) {
        super(message);
    }

    public WrongRedirectionException(Throwable cause) {
        super(cause);
    }
}
