package org.iac2.service.architecturereconstruction.plugin.implementation.manual;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.iac2.common.exception.ConfigurationMissingException;
import org.iac2.common.exception.IaCTechnologyNotSupportedException;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.utility.Utils;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelCreationPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * Refers to an externally-created EDMM instance model.
 */
public class ManualModelCreatorPlugin implements ModelCreationPlugin {
    public static final String CONFIG_ENTRY_MODEL_PATH = "modelPath";
    private static final Logger LOGGER = LoggerFactory.getLogger(ManualModelCreatorPlugin.class);
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
            this.modelPath = inputValue;
        } else {
            LOGGER.warn("Trying to set a user input not expected by the plugin (plugin-id: {})", getIdentifier());
        }
    }

    @Override
    public String getConfigurationEntry(String name) {
        if (name.equals(CONFIG_ENTRY_MODEL_PATH)) {
            return this.modelPath;
        } else {
            LOGGER.warn("Trying to get user input not used by a plugin (plugin-id: {}). Returning 'null' instead!", getIdentifier());
            return null;
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
        if (modelPath == null || modelPath.length() == 0) {
            throw new ConfigurationMissingException(getIdentifier(), CONFIG_ENTRY_MODEL_PATH);
        }

        try {
            return new InstanceModel(Utils.fetchEdmmDeploymentModel(modelPath));
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
