package org.iac2.service.fixing.plugin.implementaiton.mysql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.iac2.common.Plugin;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.service.fixing.common.interfaces.IssueFixingPluginDescriptor;

public class RemoveDBUsersFixingPluginDescriptor implements IssueFixingPluginDescriptor {
    @Override
    public String getIdentifier() {
        return "remove-mysql-db-users-fixing-plugin";
    }

    @Override
    public Collection<String> getRequiredConfigurationEntryNames() {
        return new ArrayList<>();
    }

    @Override
    public Plugin createPlugin() {
        return new RemoveDBUsersFixingPlugin(this);
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
        return Collections.emptyList();
    }
}
