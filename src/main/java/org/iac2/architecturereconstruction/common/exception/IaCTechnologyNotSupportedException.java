package org.iac2.architecturereconstruction.common.exception;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class IaCTechnologyNotSupportedException extends ArchitectureReconstructionException{
    private String iacTechnologyName;

    public IaCTechnologyNotSupportedException(String iacTechnologyName) {
        super("The following IaC technology is not supported by the plugin: " + iacTechnologyName);
    }
}
