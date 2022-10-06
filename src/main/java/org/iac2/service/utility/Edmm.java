package org.iac2.service.utility;

import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.model.support.EdmmYamlBuilder;
import org.opentosca.container.client.model.NodeInstance;

import javax.persistence.criteria.Root;
import java.util.Map;

public class Edmm {

    public static DeploymentModel addComponent(DeploymentModel deploymentModel,
                                               RootComponent rootComponent,
                                               RootComponent hostingComponent,
                                               String relationType) {

        return null;
    }
}
