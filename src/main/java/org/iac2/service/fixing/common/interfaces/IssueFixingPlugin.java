package org.iac2.service.fixing.common.interfaces;

import org.iac2.common.Plugin;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.service.fixing.common.model.IssueFixingReport;

public interface IssueFixingPlugin extends Plugin {
    IssueFixingReport fixIssue(ComplianceIssue issue, InstanceModel model, ProductionSystem productionSystem);
}
