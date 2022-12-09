package org.iac2.service.fixing.common.interfaces;

import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.service.fixing.common.model.IssueFixingReport;

import java.util.Collection;

public interface IssueFixingPlugin {
    String getIdentifier();

    boolean isSuitableForIssue(ComplianceIssue issue);

    boolean isSuitableForProductionSystem(ProductionSystem productionSystem);

    Collection<String> getRequiredPropertyNames();

    IssueFixingReport fixIssue(ComplianceIssue issue, InstanceModel model, ProductionSystem productionSystem);
}
