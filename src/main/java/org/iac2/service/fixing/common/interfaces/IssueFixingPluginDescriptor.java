package org.iac2.service.fixing.common.interfaces;

import java.util.Collection;

import org.iac2.common.PluginDescriptor;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;

public interface IssueFixingPluginDescriptor extends PluginDescriptor {
    boolean isSuitableForIssue(ComplianceIssue issue);

    boolean isIaCTechnologySupported(String iacTechnology);

    Collection<String> getRequiredProductionSystemPropertyNames();
}
