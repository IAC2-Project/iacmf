package org.iac2.common.exception;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class IaCTechnologyNotSupportedException extends IacmfException {
    private String iacTechnologyName;

    public IaCTechnologyNotSupportedException(String iacTechnologyName) {
        super("The following IaC technology is not supported by the plugin: " + iacTechnologyName);
        this.iacTechnologyName = iacTechnologyName;
    }

    public IaCTechnologyNotSupportedException(String iacTechnologyName, Throwable throwable) {
        super("The following IaC technology is not supported by the plugin: " + iacTechnologyName, throwable);
        this.iacTechnologyName = iacTechnologyName;
    }
}
