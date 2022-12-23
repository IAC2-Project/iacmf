package org.iac2.service.utility;

import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.entity.KVEntity;
import org.iac2.entity.compliancejob.execution.ExecutionEntity;
import org.iac2.entity.compliancejob.issue.ComplianceIssueEntity;
import org.iac2.entity.compliancejob.issue.IssueFixingReportEntity;
import org.iac2.service.fixing.common.model.IssueFixingReport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PojoToEntity {

    public static IssueFixingReportEntity transformFixingReport(IssueFixingReport report, ComplianceIssueEntity issue) {
        IssueFixingReportEntity reportE = new IssueFixingReportEntity(report.isSuccessful(), issue);
        reportE.setDescription(report.getDescription());

        return reportE;
    }

    public static ComplianceIssueEntity transformComplianceIssue(ExecutionEntity execution, ComplianceIssue issue) {

        ComplianceIssueEntity result = new ComplianceIssueEntity(
                execution,
                issue
                issue.getDescription(),
                issue.getType());
        result.setProperties(transformIssueProperties(result, issue.getProperties()));

        return result;
    }

    public static List<KVEntity> transformIssueProperties(
            ComplianceIssueEntity issueEntity,
            Map<String, String> properties) {
        List<KVEntity> result = new ArrayList<>();
        properties.forEach((k, v) -> result.add(new KVEntity(k, v, issueEntity)));

        return result;
    }
}
