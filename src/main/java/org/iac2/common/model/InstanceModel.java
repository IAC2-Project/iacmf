package org.iac2.common.model;

import io.github.edmm.model.DeploymentModel;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class InstanceModel {
    private final Map<String, String> properties;
    private final DeploymentModel deploymentModel;

    public InstanceModel(DeploymentModel deploymentModel) {
        this.deploymentModel = deploymentModel;
        this.properties = new HashMap<>();
    }
}


