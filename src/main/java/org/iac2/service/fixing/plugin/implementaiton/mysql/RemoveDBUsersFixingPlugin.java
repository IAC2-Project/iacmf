package org.iac2.service.fixing.plugin.implementaiton.mysql;

import java.util.ArrayList;
import java.util.Collection;

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

    @Override
    public String getIdentifier() {
        return "remove-mysql-db-users-fixing-plugin";
    }

    @Override
    public Collection<String> getRequiredConfigurationEntryNames() {
        return new ArrayList<>();
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
    public boolean isSuitableForIssue(ComplianceIssue issue) {
        // in theory, we could also check for the type of the issue component (mysql-db) and that the problem is
        // related to unexpected users.
        return issue.getType().equalsIgnoreCase("WrongAttributeValueIssue")
                && issue.getProperties().containsKey("INSTANCE_MODEL_COMPONENT_ID")
                && issue.getProperties().containsKey("CHECKER_COMPONENT_ID");
    }

    @Override
    public boolean isIaCTechnologySupported(String iacTechnology) {
        return true;
    }

    @Override
    public Collection<String> getRequiredProductionSystemPropertyNames() {
        return null;
    }

    @Override
    public IssueFixingReport fixIssue(ComplianceIssue issue, InstanceModel model, ProductionSystem productionSystem) {
        return null;
    }
}
