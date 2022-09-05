package org.iac2.service.architecturereconstruction.plugin.implementation.opentoscacontainer;

import java.util.Collection;

import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.InstanceModel;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelEnhancementPlugin;

public class OpenToscaContainerModelEnhancementPlugin implements ModelEnhancementPlugin {
    @Override
    public Collection<String> getRequiredPropertyNames() {
        return null;
    }

    @Override
    public String getIdentifier() {
        return "opentosca-container-model-enhancement-plugin";
    }

    // TODO this should add a property to the instanceModel indicating whether there is a different between the instanceModel
    // provided here, and the servicetemplate (must be retrieved).
    @Override
    public void enhanceModel(InstanceModel instanceModel, ProductionSystem productionSystem) {
        instanceModel.getProperties().put("different-from-deployment-model", "true");
    }
}
