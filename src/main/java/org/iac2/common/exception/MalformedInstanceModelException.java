package org.iac2.common.exception;

import lombok.Getter;

@Getter
public class MalformedInstanceModelException extends IacmfException {
    private final String componentId;
    private final String propertyName;

    public MalformedInstanceModelException(String componentId, String propertyName, String message) {
        super(message);
        this.componentId = componentId;
        this.propertyName = propertyName;
    }
}
