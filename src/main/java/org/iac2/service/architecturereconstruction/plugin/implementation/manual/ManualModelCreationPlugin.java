package org.iac2.service.architecturereconstruction.plugin.implementation.manual;

import org.iac2.common.PluginDescriptor;
import org.iac2.common.exception.IaCTechnologyNotSupportedException;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelCreationPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManualModelCreationPlugin implements ModelCreationPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManualModelCreationPlugin.class);
    private final ManualModelCreationPluginDescriptor descriptor;

    public ManualModelCreationPlugin(ManualModelCreationPluginDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public PluginDescriptor getDescriptor() {
        return this.descriptor;
    }

    @Override
    public void setConfigurationEntry(String inputName, String inputValue) {
        LOGGER.warn("Trying to pass user input to a plugin that does not need user inputs!");
    }

    @Override
    public String getConfigurationEntry(String name) {
        LOGGER.warn("Trying to get user input from a plugin that does not have user inputs!");
        return null;
    }

    @Override
    public InstanceModel reconstructInstanceModel(ProductionSystem productionSystem)
            throws IaCTechnologyNotSupportedException {
        if (!descriptor.isIaCTechnologySupported(productionSystem.getIacTechnologyName())) {
            throw new IaCTechnologyNotSupportedException(productionSystem.getIacTechnologyName());
        }

        return null;
    }
}
