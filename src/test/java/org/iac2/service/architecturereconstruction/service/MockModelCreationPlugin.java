package org.iac2.service.architecturereconstruction.service;

import io.github.edmm.model.DeploymentModel;
import lombok.Getter;
import lombok.SneakyThrows;
import org.iac2.common.PluginDescriptor;
import org.iac2.common.exception.IaCTechnologyNotSupportedException;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelCreationPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

@Getter
public class MockModelCreationPlugin implements ModelCreationPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(MockModelCreationPlugin.class);
    private final int numberOfComponents;

    public MockModelCreationPlugin(int numberOfComponents) {
        this.numberOfComponents = numberOfComponents;
    }

    @Override
    public PluginDescriptor getDescriptor() {
        return null;
    }

    @Override
    public String getIdentifier() {
        return "mock";
    }

    @Override
    public void setConfigurationEntry(String inputName, String inputValue) {

    }

    @Override
    public String getConfigurationEntry(String name) {
        LOGGER.warn("Trying to get user input from a plugin that does not have user inputs!");
        return null;
    }

    @SneakyThrows
    @Override
    public InstanceModel reconstructInstanceModel(ProductionSystem productionSystem) throws IaCTechnologyNotSupportedException {
        ClassPathResource resource = new ClassPathResource("edmm/four-components-hosted-on.yml");
        DeploymentModel model = DeploymentModel.of(resource.getFile());

        return new InstanceModel(model);
    }
}
