package org.iac2.service.fixing.common.interfaces;

import java.util.Collection;

import org.iac2.common.PluginDescriptor;

public interface IssueFixingPluginDescriptor extends PluginDescriptor {
    boolean isIssueTypeSupported(String issueType);

    boolean isIaCTechnologySupported(String iacTechnology);

    Collection<String> getRequiredProductionSystemPropertyNames();

    Collection<String> getRequiredComplianceRuleParameters();
}