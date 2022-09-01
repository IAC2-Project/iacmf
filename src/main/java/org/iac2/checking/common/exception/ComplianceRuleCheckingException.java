package org.iac2.checking.common.exception;

public abstract class ComplianceRuleCheckingException extends RuntimeException {
    public ComplianceRuleCheckingException() {
    }

    public ComplianceRuleCheckingException(String message) {
        super(message);
    }

    public ComplianceRuleCheckingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ComplianceRuleCheckingException(Throwable cause) {
        super(cause);
    }
}
