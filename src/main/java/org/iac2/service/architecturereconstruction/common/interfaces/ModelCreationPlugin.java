package org.iac2.service.architecturereconstruction.common.interfaces;

import org.iac2.common.exception.IaCTechnologyNotSupportedException;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.InstanceModel;

public interface ModelCreationPlugin extends ArchitectureReconstructionPlugin {
    boolean isIaCTechnologySupported(String iacTechnologyName);
    InstanceModel reconstructInstanceModel(ProductionSystem productionSystem) throws IaCTechnologyNotSupportedException;
}