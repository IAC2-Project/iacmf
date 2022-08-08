package org.iac2.architecturereconstruction.plugin.implementation.opentoscacontainer;

import org.iac2.architecturereconstruction.common.exception.IaCTechnologyNotSupportedException;
import org.iac2.architecturereconstruction.common.interfaces.ModelCreationPlugin;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.SystemModel;

public class OpenToscaContainerPlugin implements ModelCreationPlugin {
    @Override
    public String getIdentifier() {
        return "opentoscacontainerplugin";
    }

    @Override
    public boolean isIaCTechnologySupported(String iacTechnologyName) {
        return iacTechnologyName.equalsIgnoreCase("opentoscacontainer");
    }

    @Override
    public SystemModel reconstructInstanceModel(ProductionSystem productionSystem) throws IaCTechnologyNotSupportedException {
        if (!isIaCTechnologySupported(productionSystem.getIacTechnologyName())) {
            throw new IaCTechnologyNotSupportedException(productionSystem.getIacTechnologyName());
        }

        return null;
    }
}
