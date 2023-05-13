package org.iac2.service.reporting.plugin.implementation.noops;

import org.iac2.common.PluginDescriptor;
import org.iac2.service.reporting.common.interfaces.ReportingPlugin;
import org.iac2.service.reporting.common.model.ExecutionReport;

public class NoOperationReportingPlugin implements ReportingPlugin {
    private NoOperationPluginDescriptor descriptor;

    public NoOperationReportingPlugin(NoOperationPluginDescriptor descriptor) {
        this.descriptor = descriptor;
    }
    @Override
    public PluginDescriptor getDescriptor() {
        return this.descriptor;
    }

    @Override
    public void setConfigurationEntry(String inputName, String inputValue) {

    }

    @Override
    public String getConfigurationEntry(String name) {
        return null;
    }

    @Override
    public void reportExecutionOutcome(ExecutionReport report) {

    }
}
