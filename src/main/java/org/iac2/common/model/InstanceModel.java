package org.iac2.common.model;

import java.util.HashMap;
import java.util.Map;

import io.github.edmm.model.DeploymentModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstanceModel {
    private Map<String, String> properties;
    private DeploymentModel deploymentModel;

    public InstanceModel(DeploymentModel deploymentModel) {
        this.deploymentModel = deploymentModel;
        this.properties = new HashMap<>();
    }

    public void reCreateDeploymentModel() {
        this.deploymentModel = new DeploymentModel(deploymentModel.getName(), deploymentModel.getGraph());
    }
}


