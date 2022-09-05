package org.iac2.architecturereconstruction.common.exception;

import org.iac2.common.exception.IacmfException;

public abstract class ArchitectureReconstructionException extends IacmfException {
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

}
