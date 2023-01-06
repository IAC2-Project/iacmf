package org.iac2.service.fixing.plugin.implementaiton.bash;

import org.iac2.common.PluginDescriptor;
import org.iac2.common.exception.ConfigurationEntryMissingException;
import org.iac2.common.exception.IaCTechnologyNotSupportedException;
import org.iac2.common.exception.IssueNotSupportedException;
import org.iac2.common.exception.ProductionSystemPropertyMissingException;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.service.fixing.common.exception.ComplianceRuleMissingRequiredParameterException;
import org.iac2.service.fixing.common.interfaces.IssueFixingPlugin;
import org.iac2.service.fixing.common.model.IssueFixingReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BashFixingPlugin implements IssueFixingPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(BashFixingPlugin.class);

    final BashFixingPluginDescriptor descriptor;
    private String script;
    // as Kalle: how to pass arguments?
    private String arguments;

    public BashFixingPlugin(BashFixingPluginDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public PluginDescriptor getDescriptor() {
        return this.descriptor;
    }

    @Override
    public void setConfigurationEntry(String inputName, String inputValue) {
        switch (inputName) {
            case BashFixingPluginDescriptor.CONFIGURATION_ENTRY_SCRIPT -> this.script = inputName;
            case BashFixingPluginDescriptor.CONFIGURATION_ENTRY_ARGUMENTS -> this.arguments = inputName;
            default -> LOGGER.warn("Trying to set an expected configuration entry '{}'. Ignored!", inputName);
        }
    }

    @Override
    public String getConfigurationEntry(String name) {
        switch (name) {
            case BashFixingPluginDescriptor.CONFIGURATION_ENTRY_SCRIPT -> {
                return this.script;
            }
            case BashFixingPluginDescriptor.CONFIGURATION_ENTRY_ARGUMENTS -> {
                return this.arguments;
            }
            default -> {
                LOGGER.warn("Trying to get an unknown configuration entry '{}'!", name);
                return null;
            }
        }
    }

    @Override
    public IssueFixingReport fixIssue(ComplianceIssue issue, InstanceModel model, ProductionSystem productionSystem) throws ComplianceRuleMissingRequiredParameterException, ConfigurationEntryMissingException, ProductionSystemPropertyMissingException, IaCTechnologyNotSupportedException, IssueNotSupportedException {
        return null;
    }
}
