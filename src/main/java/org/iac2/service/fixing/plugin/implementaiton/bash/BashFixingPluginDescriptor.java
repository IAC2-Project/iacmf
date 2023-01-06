package org.iac2.service.fixing.plugin.implementaiton.bash;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.iac2.common.Plugin;
import org.iac2.service.fixing.common.interfaces.IssueFixingPluginDescriptor;

public class BashFixingPluginDescriptor implements IssueFixingPluginDescriptor {
    public static final String IDENTIFIER = "bash-fixing-plugin";
    public static final String CONFIGURATION_ENTRY_SCRIPT = "script";
    public static final String CONFIGURATION_ENTRY_ARGUMENTS = "script_arguments";

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Collection<String> getRequiredConfigurationEntryNames() {
        return List.of(CONFIGURATION_ENTRY_SCRIPT, CONFIGURATION_ENTRY_ARGUMENTS);
    }

    @Override
    public Plugin createPlugin() {
        return new BashFixingPlugin(this);
    }

    @Override
    public boolean isIssueTypeSupported(String issueType) {
        return true;
    }

    @Override
    public boolean isIaCTechnologySupported(String iacTechnology) {
        return true;
    }

    @Override
    public Collection<String> getRequiredProductionSystemPropertyNames() {
        return new ArrayList<>();
    }

    @Override
    public Collection<String> getRequiredComplianceRuleParameters() {
        return new ArrayList<>();
    }
}
