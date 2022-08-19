package org.iac2.architecturereconstruction.common.exception;

public class AppNotFoundException extends ArchitectureReconstructionException {

    public AppNotFoundException() {
    }

    public AppNotFoundException(String message) {
        super(message);
    }

    public AppNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppNotFoundException(Throwable cause) {
        super(cause);
    }

    public AppNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
