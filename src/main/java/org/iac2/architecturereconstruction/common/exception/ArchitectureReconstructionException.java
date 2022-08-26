package org.iac2.architecturereconstruction.common.exception;

public abstract class ArchitectureReconstructionException extends RuntimeException {
    public ArchitectureReconstructionException() {
    }

    public ArchitectureReconstructionException(String message) {
        super(message);
    }

    public ArchitectureReconstructionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArchitectureReconstructionException(Throwable cause) {
        super(cause);
    }

    public ArchitectureReconstructionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
