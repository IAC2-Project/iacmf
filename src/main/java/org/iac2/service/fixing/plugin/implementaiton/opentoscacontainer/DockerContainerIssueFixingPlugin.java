package org.iac2.service.fixing.plugin.implementaiton.opentoscacontainer;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.google.common.collect.Maps;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.HostedOn;
import org.assertj.core.util.Lists;
import org.iac2.common.exception.IaCTechnologyNotSupportedException;
import org.iac2.common.exception.IssueNotSupportedException;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.common.utility.Edmm;
import org.iac2.common.utility.Utils;
import org.iac2.service.architecturereconstruction.common.model.EdmmTypes.DockerContainer;
import org.iac2.service.architecturereconstruction.common.model.EdmmTypes.DockerEngine;
import org.iac2.service.architecturereconstruction.common.model.StructuralState;
import org.iac2.service.fixing.common.interfaces.IssueFixingPlugin;
import org.iac2.service.fixing.common.model.IssueFixingReport;

public class DockerContainerIssueFixingPlugin implements IssueFixingPlugin {
    @Override
    public String getIdentifier() {
        return "docker-container-issue-fixing-plugin";
    }

    @Override
    public boolean isSuitableForIssue(ComplianceIssue issue) {
        return issue.getType().equalsIgnoreCase("WrongAttributeValueIssue")
                && issue.getProperties().containsKey("INSTANCE_MODEL_COMPONENT_ID")
                && issue.getProperties().containsKey("CHECKER_COMPONENT_ID");
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
                .filter(c -> {
                    String structuralState = c.getProperties().get("structuralState").getValue();
                    return structuralState.equals(StructuralState.NOT_EXPECTED.toString()) | structuralState.equals(StructuralState.REMOVED.toString());
                })
                .map(c -> (DockerContainer) c)
                .forEach(c -> {
                    DockerEngine dockerEngine = this.findDockerEngine(instanceModel.getDeploymentModel().getComponents(), c);
                    if (dockerEngineCollectionMap.containsKey(dockerEngine)) {
                        dockerEngineCollectionMap.get(dockerEngine).add(c);
                    } else {
                        Collection<DockerContainer> dockerContainerCollection = Lists.newArrayList();
                        dockerContainerCollection.add(c);
                        dockerEngineCollectionMap.put(dockerEngine, dockerContainerCollection);
                    }
                });

        StringBuilder strB = new StringBuilder();
        strB.append("DockerContainerIssueFixingPlugin Report:").append("\n");

        for (DockerEngine dockerEngine : dockerEngineCollectionMap.keySet()) {
            String dockerEngineUrl = dockerEngine.getProperties().get("DockerEngineURL").getValue();

            if (dockerEngineUrl.contains("host.docker.internal")) {
                // this is a little dirty, as we use such an URL in the test environment,
                // we assume this URL is never like this but only a proper URL/IP
                // => TODO: FIXME
                dockerEngineUrl = dockerEngineUrl.replace("host.docker.internal", "localhost");
            }

            try (DockerClient dockerClient = Utils.createDockerClient(dockerEngineUrl)) {

                dockerEngineCollectionMap.get(dockerEngine).forEach(d -> {
                    if (d.getProperties().get("structuralState").getValue().equals(StructuralState.NOT_EXPECTED.toString())) {
                        // delete the container
                        String containerId = d.getProperties().get("ContainerID").getValue();
                        Container container = dockerClient.listContainersCmd().exec().stream().filter(c -> c.getId().equals(containerId)).findFirst().get();
                        dockerClient.stopContainerCmd(container.getId()).exec();
                        dockerClient.removeContainerCmd(container.getId()).exec();

                        // remove from model
                        Edmm.removeComponents(instanceModel.getDeploymentModel().getGraph(), List.of(d));
                        instanceModel.setDeploymentModel(new DeploymentModel(instanceModel.getDeploymentModel().getName(), instanceModel.getDeploymentModel().getGraph()));
                        strB.append("Removed Container ").append(containerId).append(" from DockerEngine ").append(dockerEngine.getId()).append("\n");
                    } else {
                        // TODO this here will be quite hard actually without the IaC tools, thinking about
                    }
                });
            } catch (IOException e) {
                strB.append("Failed to execute docker commands on engine: %s. Reason: %s"
                        .formatted(dockerEngine.getName(), e.getMessage()));

                return new IssueFixingReport(false, strB.toString());
            }
        }

        return new IssueFixingReport(true, strB.toString());
    }

    public DockerEngine findDockerEngine(Collection<RootComponent> components, DockerContainer dockerContainer) {
        return dockerContainer.getRelations().stream().filter(r -> r instanceof HostedOn).map(r -> (HostedOn) r).map(r -> r.getTarget()).map(t -> components.stream().filter(c -> c.getId().equals(t)).findFirst().orElse(null)).filter(c -> c != null).filter(c -> c instanceof DockerEngine).map(c -> (DockerEngine) c).findFirst().get();
    }
}
