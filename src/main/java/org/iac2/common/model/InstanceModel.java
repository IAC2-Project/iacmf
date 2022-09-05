package org.iac2.common.model;

import java.util.HashMap;
import java.util.Map;

import io.github.edmm.model.DeploymentModel;
import lombok.Getter;

@Getter
public class InstanceModel {
    private final Map<String, String> properties;
    private final DeploymentModel deploymentModel;

    public InstanceModel(DeploymentModel deploymentModel) {
        this.deploymentModel = deploymentModel;
        this.properties = new HashMap<>();
    }
}


