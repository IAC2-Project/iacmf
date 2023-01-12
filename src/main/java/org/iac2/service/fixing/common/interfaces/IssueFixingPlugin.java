package org.iac2.service.fixing.common.interfaces;

import org.iac2.common.Plugin;
import org.iac2.common.exception.IaCTechnologyNotSupportedException;
import org.iac2.common.exception.IssueNotSupportedException;
import org.iac2.common.exception.MissingConfigurationEntryException;
import org.iac2.common.exception.MissingProductionSystemPropertyException;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.service.fixing.common.exception.ComplianceRuleMissingRequiredParameterException;
import org.iac2.service.fixing.common.model.IssueFixingReport;

public interface IssueFixingPlugin extends Plugin {
    IssueFixingReport fixIssue(ComplianceIssue issue, InstanceModel model, ProductionSystem productionSystem) throws
            ComplianceRuleMissingRequiredParameterException, MissingConfigurationEntryException, MissingProductionSystemPropertyException,
            IaCTechnologyNotSupportedException, IssueNotSupportedException;
}
