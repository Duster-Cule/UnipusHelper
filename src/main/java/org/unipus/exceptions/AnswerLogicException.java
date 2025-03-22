package org.unipus.exceptions;

public class AnswerLogicException extends UnipusHelperException{
    public AnswerLogicException() {
        super();
    }

    public AnswerLogicException(String message, Throwable cause) {
        super(message, cause);
    }

    public AnswerLogicException(String message) {
        super(message);
    }

    public AnswerLogicException(Throwable cause) {
        super(cause);
    }
}