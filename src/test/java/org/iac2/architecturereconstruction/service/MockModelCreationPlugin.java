package org.iac2.architecturereconstruction.service;

import io.github.edmm.model.DeploymentModel;
import lombok.Getter;
import lombok.SneakyThrows;
import org.iac2.architecturereconstruction.common.exception.IaCTechnologyNotSupportedException;
import org.iac2.architecturereconstruction.common.interfaces.ModelCreationPlugin;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.SystemModel;
import org.springframework.core.io.ClassPathResource;

@Getter
public class MockModelCreationPlugin implements ModelCreationPlugin {
    private final int numberOfComponents;

    public MockModelCreationPlugin(int numberOfComponents) {
        this.numberOfComponents = numberOfComponents;
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
    public SystemModel reconstructInstanceModel(ProductionSystem productionSystem) throws IaCTechnologyNotSupportedException {
        SystemModel result = new SystemModel();
        ClassPathResource resource = new ClassPathResource("edmm/three-components-hosted-on.yml");
        DeploymentModel model = DeploymentModel.of(resource.getFile());
        result.setDeploymentModel(model);

        return result;
    }
}
