package org.iac2.common.exception;

public abstract class IacmfException extends RuntimeException {
    public IacmfException() {
    }

    public IacmfException(String message) {
        super(message);
    }

    public IacmfException(String message, Throwable cause) {
        super(message, cause);
    }

    public IacmfException(Throwable cause) {
        super(cause);
    }
}
