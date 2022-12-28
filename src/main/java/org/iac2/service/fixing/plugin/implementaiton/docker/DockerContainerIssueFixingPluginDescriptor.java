package org.iac2.service.fixing.plugin.implementaiton.docker;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.iac2.common.Plugin;
import org.iac2.service.fixing.common.interfaces.IssueFixingPluginDescriptor;

public class DockerContainerIssueFixingPluginDescriptor implements IssueFixingPluginDescriptor {
    public static final String IDENTIFIER = "docker-container-issue-fixing-plugin";

    public static final String[] SUPPORTED_ISSUE_TYPES = {"UNEXPECTED_DOCKER_CONTAINERS"};

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Plugin createPlugin() {
        return new DockerContainerIssueFixingPlugin(this);
    }

    @Override
    public boolean isIssueTypeSupported(String issueType) {
        return Arrays.stream(SUPPORTED_ISSUE_TYPES).anyMatch(t -> t.equalsIgnoreCase(issueType));
    }

    @Override
    public boolean isIaCTechnologySupported(String iacTechnology) {
        // this is interesting actually, as this plugin shouldn't actually care about the IaC in particular right?
        // therefore just set to true
        return true;
    }

    @Override
    public Collection<String> getRequiredProductionSystemPropertyNames() {
        return Collections.emptyList();
    }

    @Override
    public Collection<String> getRequiredComplianceRuleParameters() {
        return Collections.emptyList();
    }

    @Override
    public Collection<String> getRequiredConfigurationEntryNames() {
        return Collections.emptyList();
    }
}
