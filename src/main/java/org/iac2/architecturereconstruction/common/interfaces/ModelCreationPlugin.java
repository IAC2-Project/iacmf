package org.iac2.architecturereconstruction.common.interfaces;

import org.iac2.architecturereconstruction.common.exception.IaCTechnologyNotSupportedException;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.SystemModel;

public interface ModelCreationPlugin extends ArchitectureReconstructionPlugin {
    boolean isIaCTechnologySupported(String iacTechnologyName);
    SystemModel reconstructInstanceModel(ProductionSystem productionSystem) throws IaCTechnologyNotSupportedException;
}
