package org.iac2.common.exception;

import lombok.Getter;

@Getter
public class IaCTechnologyNotSupportedException extends IacmfException {
    private final String iacTechnologyName;

    public IaCTechnologyNotSupportedException(String iacTechnologyName) {
        super("The following IaC technology is not supported by the plugin: " + iacTechnologyName);
        this.iacTechnologyName = iacTechnologyName;
    }

    public IaCTechnologyNotSupportedException(String iacTechnologyName, Throwable throwable) {
        super("The following IaC technology is not supported by the plugin: " + iacTechnologyName, throwable);
        this.iacTechnologyName = iacTechnologyName;
    }
}
