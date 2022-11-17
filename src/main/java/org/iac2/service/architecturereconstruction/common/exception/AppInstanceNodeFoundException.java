package org.iac2.service.architecturereconstruction.common.exception;

public class AppInstanceNodeFoundException extends ArchitectureReconstructionException {

    public AppInstanceNodeFoundException() {
    }

    public AppInstanceNodeFoundException(String message) {
        super(message);
    }

    public AppInstanceNodeFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppInstanceNodeFoundException(Throwable cause) {
        super(cause);
    }
}
