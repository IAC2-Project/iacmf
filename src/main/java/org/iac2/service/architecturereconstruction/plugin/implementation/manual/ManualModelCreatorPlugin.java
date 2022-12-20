package org.iac2.service.architecturereconstruction.plugin.implementation.manual;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.iac2.common.exception.IaCTechnologyNotSupportedException;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelCreationPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * Refers to an externally-created EDMM instance model.
 */
public class ManualModelCreatorPlugin implements ModelCreationPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(ManualModelCreatorPlugin.class);
    private static final String CONFIG_ENTRY_MODEL_PATH = "modelPath";

    private String modelPath;

    @Override
    public String getIdentifier() {
        return "manual-model-creation-plugin";
    }

    @Override
    public boolean isIaCTechnologySupported(String iacTechnologyName) {
        return true;
    }

    @Override
    public Collection<String> getRequiredConfigurationEntryNames() {
        return List.of(CONFIG_ENTRY_MODEL_PATH);
    }

    @Override
    public void setConfigurationEntry(String inputName, String inputValue) {
        if (inputName.equals(CONFIG_ENTRY_MODEL_PATH)) {

        }
    }

    @Override
    public Collection<String> getRequiredProductionSystemPropertyNames() {
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
