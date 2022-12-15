package org.iac2.service.fixing.service;

import java.util.Collection;
import java.util.Collections;

import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.service.fixing.common.interfaces.IssueFixingPlugin;
import org.iac2.service.fixing.common.model.IssueFixingReport;

public class MockIssueFixingPlugin implements IssueFixingPlugin {
    @Override
    public String getIdentifier() {
        return "mock";
    }

    @Override
    public Collection<String> getRequiredConfigurationEntryNames() {
        return Collections.emptyList();
    }

    @Override
    public void setConfigurationEntry(String inputName, String inputValue) {

    }

    @Override
    public boolean isSuitableForIssue(ComplianceIssue issue) {
        return true;
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
    public IssueFixingReport fixIssue(ComplianceIssue issue, InstanceModel model, ProductionSystem productionSystem) {
        return new IssueFixingReport(true);
    }
}
