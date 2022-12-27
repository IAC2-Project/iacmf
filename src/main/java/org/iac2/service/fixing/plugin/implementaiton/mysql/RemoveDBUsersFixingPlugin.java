package org.iac2.service.fixing.plugin.implementaiton.mysql;

import io.github.edmm.model.component.RootComponent;
import org.iac2.common.PluginDescriptor;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.service.fixing.common.interfaces.IssueFixingPlugin;
import org.iac2.service.fixing.common.model.IssueFixingReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoveDBUsersFixingPlugin implements IssueFixingPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoveDBUsersFixingPlugin.class);
    private static final String MODEL_PROPERTY_NAME_USERS = "users";

    private final RemoveDBUsersFixingPluginDescriptor descriptor;

    public RemoveDBUsersFixingPlugin(RemoveDBUsersFixingPluginDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public PluginDescriptor getDescriptor() {
        return descriptor;
    }

    @Override
    public void setConfigurationEntry(String inputName, String inputValue) {
        LOGGER.warn("Trying to set user input in a plugin that does not expect user inputs!");
    }

    @Override
    public String getConfigurationEntry(String name) {
        LOGGER.warn("Trying to get user input from a plugin that does not have user inputs!");
        return null;
    }

    @Override
    public IssueFixingReport fixIssue(ComplianceIssue issue, InstanceModel model, ProductionSystem productionSystem) {

//        RootComponent ruleComponent = issue
//                .getComponent(issue.getProperties().get("CHECKER_COMPONENT_ID"))
//                .orElse(null);
        RootComponent modelComponent = model.getDeploymentModel()
                .getComponent(issue.getProperties().get("CHECKER_COMPONENT_ID"))
                .orElse(null);
        if (modelComponent == null) {
            return new IssueFixingReport(false, "Cannot find component referenced in issue!");
        }

        return null;
    }
}
