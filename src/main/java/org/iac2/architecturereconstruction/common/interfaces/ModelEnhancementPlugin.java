package org.iac2.architecturereconstruction.common.interfaces;

import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.SystemModel;

public interface ModelEnhancementPlugin extends ArchitectureReconstructionPlugin {
    void enhanceModel(SystemModel systemModel, ProductionSystem productionSystem);
}
