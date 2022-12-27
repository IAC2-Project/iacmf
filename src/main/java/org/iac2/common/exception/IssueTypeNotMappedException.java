package org.iac2.common.exception;

public class IssueTypeNotMappedException extends IacmfException {
    private String type;

    public IssueTypeNotMappedException(String type) {
        super("The following issue type is not mapped to a fixing plugin: %s".formatted(type));
        this.type = type;
    }
}
