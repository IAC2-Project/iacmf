package org.iac2.service.architecturereconstruction.common.interfaces;

import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.InstanceModel;

public interface ModelEnhancementPlugin extends ArchitectureReconstructionPlugin {
    InstanceModel enhanceModel(InstanceModel instanceModel, ProductionSystem productionSystem);
}
