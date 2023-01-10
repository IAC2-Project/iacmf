package org.iac2.service.fixing.plugin.implementaiton.mysql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.iac2.common.Plugin;
import org.iac2.common.model.PluginConfigurationEntryDescriptor;
import org.iac2.service.fixing.common.interfaces.IssueFixingPluginDescriptor;

public class RemoveDBUsersFixingPluginDescriptor implements IssueFixingPluginDescriptor {
    public static final String[] SUPPORTED_ISSUE_TYPES = {"UNEXPECTED_MYSQL_DB_USERS"};
    public static final String[] EXPECTED_COMPLIANCE_RULE_PARAMETERS = {"ALLOWED_USERS"};

    @Override
    public String getIdentifier() {
        return "remove-mysql-db-users-fixing-plugin";
    }

    @Override
    public String getDescription() {
        return "This plugin removes the permissions over MySQL databases from all the users that are not allowed to have access " +
                "to these databases as determined by the compliance rule (as determined by the `ALLOWED_USERS` compliance rule property).";
    }

    @Override
    public Collection<PluginConfigurationEntryDescriptor> getConfigurationEntryDescriptors() {
        return new ArrayList<>();
    }

    @Override
    public Plugin createPlugin() {
        return new RemoveDBUsersFixingPlugin(this);
    }

    @Override
    public boolean isIssueTypeSupported(String issueType) {
        return Arrays.stream(SUPPORTED_ISSUE_TYPES).anyMatch(t -> t.equalsIgnoreCase(issueType));
    }

    @Override
    public boolean isIaCTechnologySupported(String iacTechnology) {
        return true;
    }

    @Override
    public Collection<String> getRequiredProductionSystemPropertyNames() {
        return Collections.emptyList();
    }

    @Override
    public Collection<String> getRequiredComplianceRuleParameters() {
        return List.of(EXPECTED_COMPLIANCE_RULE_PARAMETERS);
    }
}
