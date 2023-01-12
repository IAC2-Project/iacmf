package org.iac2.service.architecturereconstruction.common.interfaces;

import org.iac2.common.exception.IaCTechnologyNotSupportedException;
import org.iac2.common.exception.MissingConfigurationEntryException;
import org.iac2.common.exception.MissingProductionSystemPropertyException;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;

public interface ModelCreationPlugin extends ArchitectureReconstructionPlugin {
    InstanceModel reconstructInstanceModel(ProductionSystem productionSystem)
            throws IaCTechnologyNotSupportedException, MissingProductionSystemPropertyException, MissingConfigurationEntryException;
}
