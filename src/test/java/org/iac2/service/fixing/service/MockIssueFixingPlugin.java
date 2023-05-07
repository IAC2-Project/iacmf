package org.iac2.service.fixing.service;

import org.iac2.common.PluginDescriptor;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.service.fixing.common.interfaces.IssueFixingPlugin;
import org.iac2.common.model.compliancejob.issue.IssueFixingReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockIssueFixingPlugin implements IssueFixingPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(MockIssueFixingPlugin.class);

    @Override
    public PluginDescriptor getDescriptor() {
        return null;
    }

    @Override
    public String getIdentifier() {
        return "mock";
    }

    @Override
    public void setConfigurationEntry(String inputName, String inputValue) {

    }

    @Override
    public String getConfigurationEntry(String name) {
        LOGGER.warn("Trying to get user input from a plugin that does not have user inputs!");
        return null;
    }

    @Override
    public IssueFixingReport fixIssue(ComplianceIssue issue, InstanceModel model, ProductionSystem productionSystem) {
        return new IssueFixingReport(true);
    }
}
