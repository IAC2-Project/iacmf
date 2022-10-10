package org.iac2.service.utility;

import com.google.common.collect.Maps;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.Property;
import io.github.edmm.model.component.*;
import io.github.edmm.model.relation.ConnectsTo;
import io.github.edmm.model.relation.HostedOn;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.model.support.EdmmYamlBuilder;
import org.apache.catalina.Host;
import org.opentosca.container.client.model.ApplicationInstance;
import org.opentosca.container.client.model.NodeInstance;
import org.opentosca.container.client.model.RelationInstance;

import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Edmm {

    public static DeploymentModel addComponent(DeploymentModel deploymentModel,
                                               RootComponent hostingComponent,
                                               Class relationType,
                                               String componentId,
                                               Map<String, String> componentProperties,
                                               Class componentType) {
        Set<RootComponent> comps = deploymentModel.getComponents();
        EdmmYamlBuilder yamlBuilder = new EdmmYamlBuilder();

        comps.forEach(c -> {
            yamlBuilder.component(c.getClass(), c.getName());
            c.getRelations().forEach(r -> {
                RootComponent target = comps.stream().filter(co -> co.getId().equals(r.getTarget())).findFirst().orElse(null);

                if (r instanceof HostedOn) {
                    yamlBuilder.hostedOn(target.getClass(), target.getId());
                } else if (r instanceof ConnectsTo) {
                    yamlBuilder.connectsTo(target.getClass(), target.getId());
                }
            });
        });

        // add new component
        yamlBuilder.component(componentType, componentId);
        if (relationType.getCanonicalName().equals(HostedOn.class.getCanonicalName())) {
            yamlBuilder.hostedOn(hostingComponent.getClass(), hostingComponent.getId());
        } else if (relationType.getCanonicalName().equals(ConnectsTo.class.getCanonicalName())) {
            yamlBuilder.connectsTo(hostingComponent.getClass(), hostingComponent.getId());
        }


        String yamlString = yamlBuilder.build();

        DeploymentModel newDeploymentModel = DeploymentModel.of(yamlString);

        comps.forEach(c -> {
            Map<String, String> props = Maps.newHashMap();
            c.getProperties().values().forEach(val -> props.put(val.getName(), val.getValue()));
            newDeploymentModel.getComponents().stream().filter(newC -> newC.getId().equals(c.getId())).toList().forEach(newC -> {
                props.forEach(newC::addProperty);
            });
        });

        newDeploymentModel.getComponents().stream().filter(c -> c.getId().equals(componentId)).findFirst().ifPresent(c -> componentProperties.forEach(c::addProperty));
        return newDeploymentModel;
    }


}
