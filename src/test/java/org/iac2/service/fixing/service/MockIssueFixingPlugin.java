package org.iac2.service.fixing.service;

import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.service.fixing.common.interfaces.IssueFixingPlugin;
import org.iac2.service.fixing.common.model.IssueFixingReport;

import java.util.Collection;
import java.util.Collections;

public class MockIssueFixingPlugin implements IssueFixingPlugin {
    @Override
    public String getIdentifier() {
        return "mock";
    }

    @Override
    public boolean isSuitableForIssue(ComplianceIssue issue) {
        return true;
    }

    @Override
    public boolean isSuitableForProductionSystem(ProductionSystem productionSystem) {
        return true;
    }

    @Override
    public Collection<String> getRequiredPropertyNames() {
        return Collections.emptyList();
    }

    @Override
    public IssueFixingReport fixIssue(ComplianceIssue issue, InstanceModel model, ProductionSystem productionSystem) {
        return new IssueFixingReport(true);
    }
}
