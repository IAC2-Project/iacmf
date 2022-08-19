package org.iac2.architecturereconstruction.common.exception;

public class InputNotValidException extends ArchitectureReconstructionException {

    public InputNotValidException() {
    }

    public InputNotValidException(String message) {
        super(message);
    }

    public InputNotValidException(String message, Throwable cause) {
        super(message, cause);
    }

    public InputNotValidException(Throwable cause) {
        super(cause);
    }

    public InputNotValidException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
