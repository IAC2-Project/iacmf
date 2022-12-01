package org.iac2.service.fixing.plugin.implementaiton.opentoscacontainer;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.google.common.collect.Maps;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.HostedOn;
import org.assertj.core.util.Lists;
import org.iac2.common.exception.IaCTechnologyNotSupportedException;
import org.iac2.common.exception.IssueNotSupportedException;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.common.utility.Utils;
import org.iac2.service.architecturereconstruction.common.model.EdmmTypes.DockerContainer;
import org.iac2.service.architecturereconstruction.common.model.EdmmTypes.DockerEngine;
import org.iac2.service.architecturereconstruction.common.model.StructuralState;
import org.iac2.service.fixing.common.interfaces.IssueFixingPlugin;
import org.iac2.service.fixing.common.model.IssueFixingReport;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class DockerContainerIssueFixingPlugin implements IssueFixingPlugin {
    @Override
    public String getIdentifier() {
        return "docker-container-issue-fixing-plugin";
    }

    @Override
    public boolean isSuitableForIssue(ComplianceIssue issue) {
        return issue.getType().equalsIgnoreCase("instance-matches-model");
    }

    @Override
    public boolean isSuitableForProductionSystem(ProductionSystem productionSystem) {
        // this is interesting actually, as this plugin shouldn't actually care about the IaC in particular right?
        // therefore just set to true
        return true;
    }

    @Override
    public Collection<String> getRequiredPropertyNames() {
        return null;
    }

    @Override
    public IssueFixingReport fixIssue(ComplianceIssue issue, InstanceModel instanceModel, ProductionSystem productionSystem) {
        if (!isSuitableForIssue(issue)) {
            throw new IssueNotSupportedException(issue);
        }

        if (!isSuitableForProductionSystem(productionSystem)) {
            // see at method isSuitableForProd... i don't think this will ever be a problem
            throw new IaCTechnologyNotSupportedException(productionSystem.getIacTechnologyName());
        }

        // find docker containers and engines


        Map<DockerEngine, Collection<DockerContainer>> dockerEngineCollectionMap = Maps.newHashMap();

        instanceModel.getDeploymentModel().getComponents().stream()
                .filter(c -> c instanceof DockerContainer)
                .filter(c -> c.getProperties().containsKey("structuralState"))
                .filter(c -> c.getProperty("structuralState").equals(StructuralState.NOT_EXPECTED) | c.getProperty("structuralState").equals(StructuralState.REMOVED))
                .map(c -> (DockerContainer) c)
                .forEach(c -> {
                    DockerEngine dockerEngine = this.findDockerEngine(instanceModel.getDeploymentModel().getComponents(), c);
                    if (dockerEngineCollectionMap.containsKey(dockerEngineCollectionMap)) {
                        dockerEngineCollectionMap.get(dockerEngineCollectionMap).add(c);
                    } else {
                        Collection<DockerContainer> dockerContainerCollection = Lists.newArrayList();
                        dockerContainerCollection.add(c);
                        dockerEngineCollectionMap.put(dockerEngine, dockerContainerCollection);
                    }
                });


        StringBuilder strB = new StringBuilder();
        strB.append("DockerContainerIssueFixingPlugin Report:").append("\n");


        dockerEngineCollectionMap.forEach((dockerEngine, dockerContainers) -> {
            DockerClient dockerClient = Utils.createDockerClient(dockerEngine.getProperties().get("DockerEngineURL").getValue());

            dockerContainers.forEach(d -> {
                if (d.getProperties().get("structuralState").equals(StructuralState.NOT_EXPECTED)) {
                    // delete the container
                    String containerId = d.getProperties().get("ContainerID").getValue();
                    Container container = dockerClient.listContainersCmd().exec().stream().filter(c -> c.getId().equals(containerId)).findFirst().get();
                    dockerClient.stopContainerCmd(container.getId()).exec();
                    dockerClient.removeContainerCmd(container.getId()).exec();
                    strB.append("Removed Container " + containerId + " from DockerEngine " + dockerEngine.getId()).append("\n");
                    // TODO should the component be removed in the deployment Model by this plugin ?
                } else {
                    // TODO this here will be quite hard actually without the IaC tools, thinking about
                }
            });
        });

        IssueFixingReport report = new IssueFixingReport(true, strB.toString());

        return report;
    }

    public DockerEngine findDockerEngine(Collection<RootComponent> components, DockerContainer dockerContainer) {
        return dockerContainer.getRelations().stream().filter(r -> r instanceof HostedOn).map(r -> r.getTarget()).map(t -> components.stream().filter(c -> c.getId().equals(t))).filter(c -> c instanceof DockerEngine).map(c -> (DockerEngine) c).findFirst().get();
    }
}
