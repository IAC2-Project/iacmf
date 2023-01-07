package org.iac2.service.fixing.plugin.implementaiton.bash;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.RootComponent;
import org.h2.store.fs.FileUtils;
import org.iac2.common.PluginDescriptor;
import org.iac2.common.exception.ConfigurationEntryMissingException;
import org.iac2.common.exception.IaCTechnologyNotSupportedException;
import org.iac2.common.exception.IssueNotSupportedException;
import org.iac2.common.exception.MalformedInstanceModelException;
import org.iac2.common.exception.PrivateKeyNotAccessibleException;
import org.iac2.common.exception.ProductionSystemPropertyMissingException;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.common.utility.VirtualMachine;
import org.iac2.service.fixing.common.exception.ComplianceRuleMissingRequiredParameterException;
import org.iac2.service.fixing.common.interfaces.IssueFixingPlugin;
import org.iac2.service.fixing.common.model.IssueFixingReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BashFixingPlugin implements IssueFixingPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(BashFixingPlugin.class);

    final BashFixingPluginDescriptor descriptor;

    /**
     * The bash script to be invoked over ssh.
     */
    private String script;

    /**
     * A comma-separated list of compliance rule parameter names. The values of these parameters will be passed to the
     * bash script as command-line arguments in the same order as in this list.
     */
    private String complianceRuleArguments;

    /**
     * The username that will be used for the ssh connection.
     */
    private String userName;

    /**
     * The private key that will be used if the instance model does not provide different information.
     */
    private String defaultPrivateKey;

    public BashFixingPlugin(BashFixingPluginDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public PluginDescriptor getDescriptor() {
        return this.descriptor;
    }

    @Override
    public void setConfigurationEntry(String inputName, String inputValue) {
        switch (inputName) {
            case BashFixingPluginDescriptor.CONFIGURATION_ENTRY_SCRIPT -> this.script = inputValue;
            case BashFixingPluginDescriptor.CONFIGURATION_ENTRY_COMPLIANCE_RULE_ARGUMENTS ->
                    this.complianceRuleArguments = inputValue;
            case BashFixingPluginDescriptor.CONFIGURATION_ENTRY_USERNAME -> this.userName = inputValue;
            case BashFixingPluginDescriptor.CONFIGURATION_ENTRY_DEFAULT_PRIVATE_KEY ->
                    this.defaultPrivateKey = inputValue;
            default -> LOGGER.warn("Trying to set an expected configuration entry '{}'. Ignored!", inputName);
        }
    }

    @Override
    public String getConfigurationEntry(String name) {
        switch (name) {
            case BashFixingPluginDescriptor.CONFIGURATION_ENTRY_SCRIPT -> {
                return this.script;
            }
            case BashFixingPluginDescriptor.CONFIGURATION_ENTRY_COMPLIANCE_RULE_ARGUMENTS -> {
                return this.complianceRuleArguments;
            }
            case BashFixingPluginDescriptor.CONFIGURATION_ENTRY_USERNAME -> {
                return this.userName;
            }
            case BashFixingPluginDescriptor.CONFIGURATION_ENTRY_DEFAULT_PRIVATE_KEY -> {
                return this.defaultPrivateKey;
            }
            default -> {
                LOGGER.warn("Trying to get an unknown configuration entry '{}'!", name);
                return null;
            }
        }
    }

    @Override
    public IssueFixingReport fixIssue(ComplianceIssue issue, InstanceModel model, ProductionSystem productionSystem) throws ComplianceRuleMissingRequiredParameterException, ConfigurationEntryMissingException, ProductionSystemPropertyMissingException, IaCTechnologyNotSupportedException, IssueNotSupportedException {
        Compute vmComponent = validateInputs(issue, model);
        String hostname = vmComponent.getProperty(Compute.PUBLIC_ADDRESS).filter(a -> !a.isEmpty()).orElseThrow(() ->
                new MalformedInstanceModelException(vmComponent.getId(), Compute.PUBLIC_ADDRESS.getName(),
                        "The component (id: %s) is missing a property '%s'".formatted(vmComponent.getId(), Compute.PUBLIC_ADDRESS.getName())));
        String privateKey = readPrivateKey(vmComponent);
        VirtualMachine virtualMachine = new VirtualMachine(hostname, null, userName, privateKey);

        StringBuilder builder = new StringBuilder();
        boolean isSuccessful = true;

        try {
            virtualMachine.connect();
            builder.append("Connected to vm (%s).\n".formatted(virtualMachine));
            String script = virtualMachine.replaceHome(this.script);
            builder.append("Replaced root with the current path.\n");
            String output = virtualMachine.execCommand(script);
            builder.append("Executed command: ")
                    .append(script)
                    .append("\n")
                    .append("The resulting output was: ")
                    .append(output);
        } catch (Exception e) {
            LOGGER.warn("Failed to fix issue. Reason: {}", e.getMessage());
            isSuccessful = false;
            builder.append("Failed to fix issue. Reason: ")
                    .append(e.getMessage());
        } finally {
            virtualMachine.disconnect();
        }

        return new IssueFixingReport(isSuccessful, builder.toString());
    }

    public String readPrivateKey(Compute vmComponent) throws PrivateKeyNotAccessibleException {
        String privateKeyPath = vmComponent.getProperty(Compute.PRIVATE_KEY_PATH).filter(p -> !p.isEmpty()).orElse(null);

        if (privateKeyPath == null) {
            LOGGER.warn("The vm component (id: {}) does not declare a path to a private key file. Looking for a default file instead!", vmComponent.getId());

            if (defaultPrivateKey == null) {
                throw new ConfigurationEntryMissingException(getIdentifier(), BashFixingPluginDescriptor.CONFIGURATION_ENTRY_DEFAULT_PRIVATE_KEY);
            }

            privateKeyPath = this.defaultPrivateKey;
        }

        if (!FileUtils.exists(privateKeyPath)) {
            throw new PrivateKeyNotAccessibleException(new FileNotFoundException("The file '%s' does not exist!".formatted(privateKeyPath)));
        }

        try {
            return Files.readString(Path.of(privateKeyPath));
        } catch (IOException e) {
            throw new PrivateKeyNotAccessibleException(e);
        }
    }

    public Compute validateInputs(ComplianceIssue issue, InstanceModel model) {
        if (script == null || script.isEmpty()) {
            throw new ConfigurationEntryMissingException(getIdentifier(), BashFixingPluginDescriptor.CONFIGURATION_ENTRY_SCRIPT);
        }

        if (userName == null || userName.isEmpty()) {
            throw new ConfigurationEntryMissingException(getIdentifier(), BashFixingPluginDescriptor.CONFIGURATION_ENTRY_USERNAME);
        }

        if (complianceRuleArguments != null && !complianceRuleArguments.isEmpty()) {
            String[] names = complianceRuleArguments.split(",");

            String missingCRParam = Arrays.stream(names)
                    .filter(pName -> issue.getRule().getParameterAssignments().stream().noneMatch(p -> p.getName().equals(pName)))
                    .findFirst().orElse(null);

            if (missingCRParam != null) {
                throw new ComplianceRuleMissingRequiredParameterException(issue.getRule(), missingCRParam);
            }
        }

        if (!issue.getProperties().containsKey("CHECKER_COMPONENT_ID")) {
            throw new IssueNotSupportedException(issue.getType(),
                    "The issue is missing a required property: '%s'".formatted("CHECKER_COMPONENT_ID"));
        }

        String vmNodeId = issue.getProperties().get("CHECKER_COMPONENT_ID");
        RootComponent component = model.getDeploymentModel().getComponent(vmNodeId).orElseThrow(() -> new MalformedInstanceModelException(vmNodeId, null,
                "The plugin (id:%s) is trying to access a missing component (id:%s)".formatted(getIdentifier(), vmNodeId)));

        if (!(component instanceof Compute)) {
            throw new MalformedInstanceModelException(vmNodeId, null,
                    "The plugin (id: %s) expects the component (id: %s) to be of type '%s' but it is instead of type '%s'".formatted(
                            getIdentifier(), vmNodeId, Compute.class.getName(), component.getClass().getName()));
        }

        String osFamily = component.getProperty(Compute.OS_FAMILY).orElse(null);

        if (osFamily == null || osFamily.isEmpty()) {
            throw new MalformedInstanceModelException(vmNodeId, Compute.OS_FAMILY.getName(),
                    "The component (id: %s) is missing a property '%s'".formatted(vmNodeId, Compute.OS_FAMILY.getName()));
        }

        if (!"linux".equalsIgnoreCase(osFamily)) {
            throw new MalformedInstanceModelException(vmNodeId, Compute.OS_FAMILY.getName(),
                    "The plugin (id: %s) expects the component (id: %s) to represent a linux-based OS, but was '%s' instead."
                            .formatted(getIdentifier(), vmNodeId, osFamily));
        }

        String machineImage = component.getProperty(Compute.MACHINE_IMAGE).orElse(null);

        if (machineImage == null || machineImage.isEmpty()) {
            throw new MalformedInstanceModelException(vmNodeId, Compute.MACHINE_IMAGE.getName(),
                    "The component (id: %s) is missing a property '%s'".formatted(vmNodeId, Compute.MACHINE_IMAGE.getName()));
        }

        if (!"ubuntu".equalsIgnoreCase(machineImage)) {
            throw new MalformedInstanceModelException(vmNodeId, Compute.MACHINE_IMAGE.getName(),
                    "The plugin (id: %s) expects the component (id: %s) to represent a linux-based (ubuntu) OS , but was a linux-based (%s) OS instead."
                            .formatted(getIdentifier(), vmNodeId, machineImage));
        }

        return (Compute) component;
    }
}
