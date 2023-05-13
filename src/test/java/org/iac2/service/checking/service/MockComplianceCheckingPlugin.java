package org.iac2.service.checking.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.iac2.common.PluginDescriptor;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.service.checking.common.exception.ComplianceRuleTypeNotSupportedException;
import org.iac2.service.checking.common.interfaces.ComplianceRuleCheckingPlugin;
import org.iac2.service.checking.common.model.RuleValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockComplianceCheckingPlugin implements ComplianceRuleCheckingPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(MockComplianceCheckingPlugin.class);

    @Override
    public void setConfigurationEntry(String key, String value) {

    }

    @Override
    public String getConfigurationEntry(String name) {
        LOGGER.warn("Trying to get user input from a plugin that does not have user inputs!");
        return null;
    }

    @Override
    public RuleValidationResult isComplianceRuleValid(ComplianceRule complianceRule) throws ComplianceRuleTypeNotSupportedException {
        return new RuleValidationResult("everything is fine!!") {
            @Override
            public boolean isValid() {
                return true;
            }
        };
    }

    @Override
    public PluginDescriptor getDescriptor() {
        return null;
    }

    @Override
    public String getIdentifier() {
        return "property-value-checker-plugin";
    }

    @Override
    public Collection<ComplianceIssue> findIssues(InstanceModel instanceModel, ComplianceRule rule) {
        ComplianceIssue issue1 = new ComplianceIssue(
                "your architecture sucks!",
                rule,
                "instance-matches-model",
                new HashMap<>());
        ComplianceIssue issue2 = new ComplianceIssue(
                "your architecture really sucks!",
                rule,
                "user-permissions",
                new HashMap<>());

        return List.of(issue1, issue2);
    }
}
