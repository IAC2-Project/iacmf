package org.iac2.service.utility;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.compliancejob.execution.Execution;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.common.model.compliancejob.issue.IssueFixingReport;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.entity.KVEntity;
import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.compliancejob.ComplianceRuleConfigurationEntity;
import org.iac2.entity.compliancejob.execution.ExecutionEntity;
import org.iac2.entity.compliancejob.issue.ComplianceIssueEntity;
import org.iac2.entity.compliancejob.issue.IssueFixingReportEntity;
import org.iac2.entity.compliancerule.ComplianceRuleEntity;
import org.iac2.entity.compliancerule.parameter.ComplianceRuleParameterAssignmentEntity;
import org.iac2.entity.productionsystem.ProductionSystemEntity;

public class EntityToPojo {

    public static ComplianceIssue transformIssue(ComplianceIssueEntity complianceIssue) {
        ComplianceRule rule = transformComplianceRule(complianceIssue.getComplianceRuleConfiguration());

        return new ComplianceIssue(
                complianceIssue.getDescription(),
                rule,
                complianceIssue.getType(),
                transformProperties(complianceIssue.getProperties())
        );
    }

    public static ComplianceRule transformComplianceRule(ComplianceRuleConfigurationEntity complianceRuleConfiguration) {
        ComplianceRuleEntity complianceRule = complianceRuleConfiguration.getComplianceRule();
        Collection<ComplianceRuleParameterAssignmentEntity> assignments = complianceRuleConfiguration.getComplianceRuleParameterAssignments();
        String issueType = complianceRuleConfiguration.getIssueType();
        ComplianceRule myCR = new ComplianceRule(complianceRule.getId(), complianceRule.getType(), complianceRule.getLocation(), issueType);

        if (assignments != null) {
            assignments.forEach(assignment -> {
                switch (assignment.getType()) {
                    case STRING -> myCR.addStringParameter(assignment.getName(), assignment.getValue());
                    case INT -> myCR.addIntParameter(assignment.getName(), assignment.getIntegerValue().orElseThrow());
                    case DECIMAL ->
                            myCR.addDoubleParameter(assignment.getName(), assignment.getDoubleValue().orElseThrow());
                    case STRING_LIST ->
                            myCR.addStringCollectionParameter(assignment.getName(), assignment.getStringListValue().orElseThrow());
                    case BOOLEAN ->
                            myCR.addBooleanParameter(assignment.getName(), assignment.getBooleanValue().orElseThrow());
                }
            });
        }

        return myCR;
    }

    public static IssueFixingReport transformIssueFixingReport(IssueFixingReportEntity entity) {
        return new IssueFixingReport(entity.getIsSuccessful(), entity.getDescription());
    }

    public static Execution transformExecution(ExecutionEntity entity) {
        return new Execution(entity.getId(), entity.getStartTime(), entity.getEndTime(), entity.getCurrentStep(),
                entity.getStatus(), entity.getViolationsDetected(), entity.getDescription());
    }


    public static ProductionSystem transformProductionSystemEntity(ProductionSystemEntity productionSystemEntity) {
        Map<String, String> properties = new HashMap<>();
        productionSystemEntity.getProperties().forEach(kvPair -> properties.put(kvPair.getKey(), kvPair.getValue()));
        return new ProductionSystem(productionSystemEntity.getName(), productionSystemEntity.getIacTechnologyName(),
                productionSystemEntity.getDescription(), properties);
    }

    public static Map<String, String> transformProperties(Collection<KVEntity> keyValuePairs) {
        Map<String, String> result = new HashMap<>();

        for (KVEntity pair : keyValuePairs) {
            result.put(pair.getKey(), pair.getValue());
        }

        return result;
    }
}
