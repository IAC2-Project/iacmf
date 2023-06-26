package org.iac2.service.reporting.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.compliancejob.execution.Execution;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.common.model.compliancejob.issue.IssueFixingReport;

import java.util.Map;

@Data
@AllArgsConstructor
public class ExecutionReport {
    private Execution execution;
    private Map<ComplianceIssue, IssueFixingReport> fixingReports;
    private ProductionSystem productionSystem;
}
