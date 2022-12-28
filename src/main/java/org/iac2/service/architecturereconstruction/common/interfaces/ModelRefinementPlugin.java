package org.iac2.service.architecturereconstruction.common.interfaces;

import org.iac2.common.exception.ConfigurationEntryMissingException;
import org.iac2.common.exception.ProductionSystemPropertyMissingException;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;

public interface ModelRefinementPlugin extends ArchitectureReconstructionPlugin {
    InstanceModel refineModel(InstanceModel instanceModel, ProductionSystem productionSystem)
            throws ProductionSystemPropertyMissingException, ConfigurationEntryMissingException;
}
