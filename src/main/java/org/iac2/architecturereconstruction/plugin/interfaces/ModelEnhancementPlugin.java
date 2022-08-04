package org.iac2.architecturereconstruction.plugin.interfaces;

import org.iac2.common.model.SystemModel;

public interface ModelEnhancementPlugin extends ArchitectureReconstructionPlugin{
    void enhanceModel(SystemModel systemModel);
}
