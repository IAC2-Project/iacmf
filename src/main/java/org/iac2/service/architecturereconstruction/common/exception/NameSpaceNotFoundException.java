

package org.iac2.service.architecturereconstruction.common.exception;

public class NameSpaceNotFoundException extends ArchitectureReconstructionException {

    public NameSpaceNotFoundException() {
    }

    public NameSpaceNotFoundException(String message) {
        super(message);
    }

    public NameSpaceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NameSpaceNotFoundException(Throwable cause) {
        super(cause);
    }
}