package org.iac2.service.architecturereconstruction.plugin.implementation.manual;

import org.iac2.common.exception.IaCTechnologyNotSupportedException;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelCreationPlugin;

import java.util.Collection;
import java.util.Collections;

public class ManualModelCreatorPlugin implements ModelCreationPlugin {
    @Override
    public String getIdentifier() {
        return "manualplugin";
    }

    @Override
    public boolean isIaCTechnologySupported(String iacTechnologyName) {
        return iacTechnologyName.equalsIgnoreCase("opentoscacontainer");
    }

    @Override
    public Collection<String> getRequiredPropertyNames() {
        return Collections.emptyList();
    }

    @Override
    public InstanceModel reconstructInstanceModel(ProductionSystem productionSystem)
            throws IaCTechnologyNotSupportedException {
        if (!isIaCTechnologySupported(productionSystem.getIacTechnologyName())) {
            throw new IaCTechnologyNotSupportedException(productionSystem.getIacTechnologyName());
        }

        return null;
    }
}
