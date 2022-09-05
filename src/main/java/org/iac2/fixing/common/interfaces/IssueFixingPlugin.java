package org.iac2.fixing.common.interfaces;

import java.util.Collection;

import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.fixing.common.model.IssueFixingReport;

public interface IssueFixingPlugin {
    String getIdentifier();
    boolean isSuitableForIssue(ComplianceIssue issue);
    boolean isSuitableForProductionSystem(ProductionSystem productionSystem);
    Collection<String> getRequiredPropertyNames();
    IssueFixingReport fixIssue(ComplianceIssue issue, ProductionSystem productionSystem);
}
