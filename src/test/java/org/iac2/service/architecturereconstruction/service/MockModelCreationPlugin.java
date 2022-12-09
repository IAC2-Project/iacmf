package org.iac2.service.architecturereconstruction.service;

import io.github.edmm.model.DeploymentModel;
import lombok.Getter;
import lombok.SneakyThrows;
import org.iac2.common.exception.IaCTechnologyNotSupportedException;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelCreationPlugin;
import org.springframework.core.io.ClassPathResource;

import java.util.Collection;
import java.util.Collections;

@Getter
public class MockModelCreationPlugin implements ModelCreationPlugin {
    private final int numberOfComponents;

    public MockModelCreationPlugin(int numberOfComponents) {
        this.numberOfComponents = numberOfComponents;
    }

    @Override
    public Collection<String> getRequiredPropertyNames() {
        return Collections.emptyList();
    }

    @Override
    public String getIdentifier() {
        return "mock";
    }

    @Override
    public boolean isIaCTechnologySupported(String iacTechnologyName) {
        return true;
    }

    @SneakyThrows
    @Override
    public InstanceModel reconstructInstanceModel(ProductionSystem productionSystem) throws IaCTechnologyNotSupportedException {
        ClassPathResource resource = new ClassPathResource("edmm/four-components-hosted-on.yml");
        DeploymentModel model = DeploymentModel.of(resource.getFile());

        return new InstanceModel(model);
    }
}
