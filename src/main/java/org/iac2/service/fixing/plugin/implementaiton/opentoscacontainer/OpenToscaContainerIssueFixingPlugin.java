package org.iac2.service.fixing.plugin.implementaiton.opentoscacontainer;

import java.util.Collection;
import java.util.Collections;

import org.iac2.common.exception.IaCTechnologyNotSupportedException;
import org.iac2.common.exception.IssueNotSupportedException;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.service.fixing.common.interfaces.IssueFixingPlugin;
import org.iac2.service.fixing.common.model.IssueFixingReport;

public class OpenToscaContainerIssueFixingPlugin implements IssueFixingPlugin {
    @Override
    public String getIdentifier() {
        return "opentosca-container-issue-fixing-plugin";
    }

    @Override
    public boolean isSuitableForIssue(ComplianceIssue issue) {
        return issue.getType().equalsIgnoreCase("instance-matches-model");
    }

    @Override
    public boolean isSuitableForProductionSystem(ProductionSystem productionSystem) {
        return productionSystem
                .getIacTechnologyName()
                .equalsIgnoreCase("opentoscacontainer");
    }

    @Override
    public Collection<String> getRequiredProductionSystemPropertyNames() {
        return Collections.emptyList();
    }

    @Override
    public Collection<String> getRequiredConfigurationEntryNames() {
        return Collections.emptyList();
    }

    @Override
    public void setConfigurationEntry(String inputName, String inputValue) {

    }

    @Override
    public IssueFixingReport fixIssue(ComplianceIssue issue, InstanceModel instanceModel, ProductionSystem productionSystem) {
        if (!isSuitableForIssue(issue)) {
            throw new IssueNotSupportedException(issue);
        }

        if (!isSuitableForProductionSystem(productionSystem)) {
            throw new IaCTechnologyNotSupportedException(productionSystem.getIacTechnologyName());
        }

        return null;
    }
}
