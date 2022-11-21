package org.iac2.service.checking.plugin.implementation.subgraphmatching;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.service.checking.common.exception.ComplianceRuleMalformattedException;
import org.iac2.service.checking.common.exception.ComplianceRuleTypeNotSupportedException;
import org.iac2.service.checking.common.interfaces.ComplianceRuleCheckingPlugin;
import org.iac2.service.checking.common.interfaces.RuleValidationResult;
import org.jgrapht.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubgraphMatchingCheckingPlugin implements ComplianceRuleCheckingPlugin {
    public static final String PLUGIN_ID = "subgraph-matching-checking-plugin";
    public static final String ISSUE_WRONG_ATTRIBUTE_VALUE = "WrongAttributeValueIssue";
    public static final String ISSUE_PROPERTY_INSTANCE_MODEL_COMPONENT_NAME = "INSTANCE_MODEL_COMPONENT_ID";
    public static final String ISSUE_PROPERTY_CHECKER_COMPONENT_NAME = "CHECKER_COMPONENT_ID";
    private static final Logger LOGGER = LoggerFactory.getLogger(SubgraphMatchingCheckingPlugin.class);
    private static final String requiredStructureSegment = "/requiredstructure/edmm/export?edmmUseAbsolutePaths=true";
    private static final String identifierSegment = "/identifier/edmm/export?edmmUseAbsolutePaths=true";

    @Override
    public Set<String> requiredConfiguration() {
        return new HashSet<>();
    }

    @Override
    public void setConfiguration(String key, String value) {
        if (!requiredConfiguration().contains(key)) {
            throw new IllegalArgumentException("The configuration key '" + "' is not expected.");
        }
    }

    @Override
    public boolean isSuitableForComplianceRule(ComplianceRule complianceRule) {
        return complianceRule.getType().equals("subgraph-matching");
    }

    @Override
    public RuleValidationResult isComplianceRuleValid(ComplianceRule complianceRule) throws
            ComplianceRuleTypeNotSupportedException,
            URISyntaxException,
            IOException,
            InterruptedException {
        final String ruleLocation = complianceRule.getLocation();
        Graph<RootComponent, RootRelation> checker = getRulePart(ruleLocation.concat(requiredStructureSegment));
        Graph<RootComponent, RootRelation> selector = getRulePart(ruleLocation.concat(identifierSegment));

        return RuleValidator.validateComplianceRule(complianceRule, selector, checker);
    }

    @Override
    public String getIdentifier() {
        return PLUGIN_ID;
    }

    @Override
    public Collection<ComplianceIssue> findIssues(InstanceModel instanceModel, ComplianceRule rule) throws ComplianceRuleTypeNotSupportedException,
            ComplianceRuleMalformattedException {
        if (!isSuitableForComplianceRule(rule)) {
            LOGGER.error("Rule '{}' is not suitable for plugin: {}", rule.getId(), getIdentifier());
            throw new ComplianceRuleTypeNotSupportedException(rule.getType());
        }

        final String ruleLocation = rule.getLocation();

        try {
            Graph<RootComponent, RootRelation> checker = getRulePart(ruleLocation.concat(requiredStructureSegment));
            Graph<RootComponent, RootRelation> selector = getRulePart(ruleLocation.concat(identifierSegment));
            RuleChecker ruleChecker = new RuleChecker(instanceModel);
            RuleCheckingResult result = ruleChecker.checkCompliance(rule, selector, checker);

            if (result.getOutcome() == RuleCheckingOutcome.INVALID_RULE) {
                String message = String.format("The compliance rule: '%s' is malformed. Reason: %s",
                        rule.getId(),
                        result.getErrorMessage());
                LOGGER.error(message);
                throw new ComplianceRuleMalformattedException(message);
            } else if (result.getOutcome() == RuleCheckingOutcome.COMPLIANCE_VIOLATION) {
                ComplianceIssue issue = new ComplianceIssue(
                        result.getErrorMessage(),
                        rule,
                        ISSUE_WRONG_ATTRIBUTE_VALUE,
                        Map.of(ISSUE_PROPERTY_INSTANCE_MODEL_COMPONENT_NAME, result.getInstanceModelComponent().getName(),
                                ISSUE_PROPERTY_CHECKER_COMPONENT_NAME, result.getCheckerComponent().getName())
                );

                return List.of(issue);
            }
        } catch (URISyntaxException | InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

        return new ArrayList<>();
    }

    public Graph<RootComponent, RootRelation> getRulePart(String fullUrl) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(new URI(fullUrl))
                .GET()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String edmmModel = response.body();
        DeploymentModel model = DeploymentModel.of(edmmModel);

        return model.getTopology();
    }
}
