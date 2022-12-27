package org.iac2.api.model;

import java.io.StringWriter;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.iac2.common.model.InstanceModel;

@Data
@AllArgsConstructor
public class InstanceModelPojo {
    private String edmmModel;
    private Map<String, String> properties;

    public InstanceModelPojo(InstanceModel instanceModel) {
        StringWriter writer = new StringWriter();
        instanceModel.getDeploymentModel().getGraph().generateYamlOutput(writer);
        this.edmmModel = writer.toString();
        this.properties = instanceModel.getProperties();
    }
}
