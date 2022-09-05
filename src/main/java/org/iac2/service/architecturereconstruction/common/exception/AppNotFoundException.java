package org.iac2.service.architecturereconstruction.common.exception;

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

}
