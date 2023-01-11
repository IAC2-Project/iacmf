package org.iac2.service.fixing.plugin.implementaiton.bash;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.iac2.common.Plugin;
import org.iac2.common.model.PluginConfigurationEntryDescriptor;
import org.iac2.common.model.PluginConfigurationEntryType;
import org.iac2.service.fixing.common.interfaces.IssueFixingPluginDescriptor;

public class BashFixingPluginDescriptor implements IssueFixingPluginDescriptor {
    public static final String IDENTIFIER = "bash-fixing-plugin";
    public static final String CONFIGURATION_ENTRY_SCRIPT = "script";
    public static final String CONFIGURATION_ENTRY_COMPLIANCE_RULE_ARGUMENTS = "compliance-rule-arguments";
    public static final String CONFIGURATION_ENTRY_USERNAME = "username";
    public static final String CONFIGURATION_ENTRY_DEFAULT_PRIVATE_KEY_PATH = "default-private-key-path";

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public String getDescription() {
        return "This plugin allows executing bash commands on an ubuntu (virtual-)machine via ssh for the purpose of fixing a detected compliance issue.";
    }

    @Override
    public Collection<PluginConfigurationEntryDescriptor> getConfigurationEntryDescriptors() {
        return List.of(
                new PluginConfigurationEntryDescriptor(
                        CONFIGURATION_ENTRY_SCRIPT,
                        PluginConfigurationEntryType.BASH_COMMAND,
                        true,
                        "The bash command that will be executed on the ubuntu (virtual-)machine via ssh."
                ),
                new PluginConfigurationEntryDescriptor(
                        CONFIGURATION_ENTRY_USERNAME,
                        PluginConfigurationEntryType.STRING,
                        true,
                        "The username to be used when connecting to the ubuntu (virtual-)machine over ssh."
                ),
                new PluginConfigurationEntryDescriptor(
                        CONFIGURATION_ENTRY_COMPLIANCE_RULE_ARGUMENTS,
                        PluginConfigurationEntryType.STRING,
                        false,
                        "a comma-separated list of compliance rule parameter names. If this value is set, the " +
                                "plugin will retrieve the referenced attributes and pass their values to the bash script as" +
                                " command-line arguments in the same order specified in this list."
                ),
                new PluginConfigurationEntryDescriptor(
                        CONFIGURATION_ENTRY_DEFAULT_PRIVATE_KEY_PATH,
                        PluginConfigurationEntryType.FILE_PATH,
                        false,
                        "The path (on the iacmf server!!) to the private key that allows to connect to the ubuntu (virtual-)machine. " +
                                "This entry will be used only if the instance model node that has the issue does not define a property called " +
                                "private_key_path. At least one of these two entries must be set."
                )
        );
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
