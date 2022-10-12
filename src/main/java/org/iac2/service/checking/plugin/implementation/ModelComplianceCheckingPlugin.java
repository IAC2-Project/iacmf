package org.iac2.service.checking.plugin.implementation;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import org.assertj.core.util.Sets;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.service.checking.common.interfaces.ComplianceRuleCheckingPlugin;
import org.opentosca.container.client.impl.SwaggerContainerClient;
import org.opentosca.container.client.model.ApplicationInstance;
import org.opentosca.container.client.model.NodeInstance;
import org.opentosca.container.client.model.RelationInstance;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ModelComplianceCheckingPlugin implements ComplianceRuleCheckingPlugin {
    @Override
    public boolean isSuitableForComplianceRule(ComplianceRule complianceRule) {
        return complianceRule.getType().equals("modelCompliance");
    }

    @Override
    public String getIdentifier() {
        return "opentosca-modelcompliance-checking-plugin";
    }

    @Override
    public Collection<ComplianceIssue> findIssues(InstanceModel instanceModel, ComplianceRule rule) {
        if (!rule.getType().equals("modelCompliance")) {
            return Sets.newHashSet();
        }
        Collection<ComplianceIssue> issues = Lists.newArrayList();
        Set<RootComponent> modelComponents = instanceModel.getDeploymentModel().getComponents();
        Set<RootRelation> modelRelations = instanceModel.getDeploymentModel().getRelations();


        String iacToolUrl = rule.getParameterAssignments().stream().filter(p -> p.getName().equals("iacToolUrl")).findFirst().get().getValueAsString();
        String instanceid = rule.getParameterAssignments().stream().filter(p -> p.getName().equals("instanceId")).findFirst().get().getValueAsString();
        String appId = rule.getParameterAssignments().stream().filter(p -> p.getName().equals("appId")).findFirst().get().getValueAsString();

        SwaggerContainerClient client = new SwaggerContainerClient(iacToolUrl, 10000);
        ApplicationInstance instance = client.getApplicationInstance(client.getApplication(appId).get(), instanceid).get();

        List<NodeInstance> nodeInstanceList = instance.getNodeInstances();
        List<RelationInstance> relationInstanceList = instance.getRelationInstances();

        if (modelComponents
                .stream()
                .filter(c -> nodeInstanceList.stream()
                .filter(n -> n.getId().equals(c.getId())).findFirst().isPresent())
                .collect(Collectors.toList()).size() != modelComponents.size()) {
            issues.add(new ComplianceIssue("", rule, "notCompliantToModel", Maps.newHashMap()));
        }

        if (modelRelations
                .stream()
                .filter(c -> relationInstanceList.stream()
                        .filter(n -> n.getId().equals(c.getId())).findFirst().isPresent())
                .collect(Collectors.toList()).size() != modelRelations.size()) {
            issues.add(new ComplianceIssue("", rule, "notCompliantToModel", Maps.newHashMap()));
        }

        return issues;
    }
}
