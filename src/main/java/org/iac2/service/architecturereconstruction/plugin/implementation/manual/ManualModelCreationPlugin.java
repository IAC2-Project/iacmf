package org.iac2.service.architecturereconstruction.plugin.implementation.manual;

import java.io.IOException;
import java.net.URISyntaxException;

import org.iac2.common.PluginDescriptor;
import org.iac2.common.exception.IaCTechnologyNotSupportedException;
import org.iac2.common.exception.MissingConfigurationEntryException;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.utility.Utils;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelCreationPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * Refers to an externally-created EDMM instance model.
 */
public class ManualModelCreationPlugin implements ModelCreationPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManualModelCreationPlugin.class);
    private final ManualModelCreationPluginDescriptor descriptor;
    private String modelPath;

    public ManualModelCreationPlugin(ManualModelCreationPluginDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public PluginDescriptor getDescriptor() {
        return this.descriptor;
    }

    @Override
    public void setConfigurationEntry(String inputName, String inputValue) {
        if (inputName.equals(ManualModelCreationPluginDescriptor.CONFIG_ENTRY_MODEL_PATH)) {
            this.modelPath = inputValue;
        } else {
            LOGGER.warn("Trying to set a user input not expected by the plugin (plugin-id: {})", getIdentifier());
        }
    }

    @Override
    public String getConfigurationEntry(String name) {
        if (name.equals(ManualModelCreationPluginDescriptor.CONFIG_ENTRY_MODEL_PATH)) {
            return this.modelPath;
        } else {
            LOGGER.warn("Trying to get user input not used by a plugin (plugin-id: {}). Returning 'null' instead!", getIdentifier());
            return null;
        }
    }

    @Override
    public InstanceModel reconstructInstanceModel(ProductionSystem productionSystem)
            throws IaCTechnologyNotSupportedException {
        if (!descriptor.isIaCTechnologySupported(productionSystem.getIacTechnologyName())) {
            throw new IaCTechnologyNotSupportedException(productionSystem.getIacTechnologyName());
        }
        if (modelPath == null || modelPath.length() == 0) {
            throw new MissingConfigurationEntryException(getIdentifier(), ManualModelCreationPluginDescriptor.CONFIG_ENTRY_MODEL_PATH);
        }

        try {
            return new InstanceModel(Utils.fetchEdmmDeploymentModel(modelPath));
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
