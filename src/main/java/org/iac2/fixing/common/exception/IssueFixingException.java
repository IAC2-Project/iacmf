package org.iac2.fixing.common.exception;

import org.iac2.common.exception.IacmfException;

public abstract class IssueFixingException extends IacmfException {
    public IssueFixingException() {
    }

    public IssueFixingException(String message) {
        super(message);
    }

    public IssueFixingException(String message, Throwable cause) {
        super(message, cause);
    }

    public IssueFixingException(Throwable cause) {
        super(cause);
    }
}
