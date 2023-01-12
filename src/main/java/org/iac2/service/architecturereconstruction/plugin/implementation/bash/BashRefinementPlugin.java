package org.iac2.service.architecturereconstruction.plugin.implementation.bash;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import io.github.edmm.model.component.Compute;
import org.apache.commons.lang3.EnumUtils;
import org.iac2.common.PluginDescriptor;
import org.iac2.common.exception.MalformedConfigurationEntryException;
import org.iac2.common.exception.MalformedInstanceModelException;
import org.iac2.common.exception.MissingConfigurationEntryException;
import org.iac2.common.exception.MissingProductionSystemPropertyException;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.compliancerule.ParameterType;
import org.iac2.common.utility.Edmm;
import org.iac2.common.utility.Utils;
import org.iac2.common.utility.VirtualMachine;
import org.iac2.service.architecturereconstruction.common.exception.BashCommandExecutionException;
import org.iac2.service.architecturereconstruction.common.exception.WrongOutputTypeException;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelRefinementPlugin;
import org.iac2.service.fixing.plugin.implementaiton.bash.BashFixingPluginDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BashRefinementPlugin implements ModelRefinementPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(BashRefinementPlugin.class);
    private final BashRefinementPluginDescriptor descriptor;

    private String script;
    private String userName;
    private String defaultPrivateKey;
    private String outputPropertyName;
    private ParameterType outputPropertyType;
    private Collection<String> productionSystemArguments;
    private boolean ignoreMissingProperties;

    public BashRefinementPlugin(BashRefinementPluginDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public Object parseOutput(String output) {
        switch (outputPropertyType) {
            case INT -> {
                return Long.valueOf(output);
            }
            case DECIMAL -> {
                return Double.valueOf(output);
            }
            case BOOLEAN -> {
                return Boolean.parseBoolean(output);
            }
            case STRING_LIST -> {
                return Arrays.stream(output.split(",")).map(String::trim).toList();
            }
            default -> {
                return output;
            }
        }
    }

    @Override
    public PluginDescriptor getDescriptor() {
        return descriptor;
    }

    @Override
    public void setConfigurationEntry(String inputName, String inputValue) {
        switch (inputName) {
            case BashRefinementPluginDescriptor.CONFIGURATION_ENTRY_SCRIPT -> this.script = inputValue;
            case BashRefinementPluginDescriptor.CONFIGURATION_ENTRY_PRODUCTION_SYSTEM_ARGUMENTS -> {
                if (inputValue == null) {
                    this.productionSystemArguments = null;
                } else {
                    this.productionSystemArguments = Arrays.stream(inputValue.split(",")).map(String::trim).toList();
                }
            }

            case BashRefinementPluginDescriptor.CONFIGURATION_ENTRY_USERNAME -> this.userName = inputValue;
            case BashRefinementPluginDescriptor.CONFIGURATION_ENTRY_DEFAULT_PRIVATE_KEY ->
                    this.defaultPrivateKey = inputValue;
            case BashRefinementPluginDescriptor.CONFIGURATION_ENTRY_OUTPUT_PROPERTY_NAME ->
                    this.outputPropertyName = inputValue;
            case BashRefinementPluginDescriptor.CONFIGURATION_ENTRY_OUTPUT_PROPERTY_TYPE -> {
                if (inputValue == null) {
                    this.outputPropertyName = null;
                } else {
                    ParameterType temp = EnumUtils.getEnum(ParameterType.class, inputValue);

                    if (temp == null) {
                        throw new MalformedConfigurationEntryException(getIdentifier(), BashRefinementPluginDescriptor.CONFIGURATION_ENTRY_OUTPUT_PROPERTY_TYPE);
                    }

                    this.outputPropertyType = temp;
                }
            }
            // anything but "true" (ignore case) is considered false (even null is considered false).
            case BashRefinementPluginDescriptor.CONFIGURATION_ENTRY_IGNORE_MISSING_PROPERTIES ->
                    this.ignoreMissingProperties = Boolean.parseBoolean(inputValue);

            default -> LOGGER.warn("Trying to set an expected configuration entry '{}'. Ignored!", inputName);
        }
    }

    @Override
    public String getConfigurationEntry(String name) {
        switch (name) {
            case BashRefinementPluginDescriptor.CONFIGURATION_ENTRY_SCRIPT -> {
                return this.script;
            }
            case BashRefinementPluginDescriptor.CONFIGURATION_ENTRY_PRODUCTION_SYSTEM_ARGUMENTS -> {
                if (productionSystemArguments == null) {
                    return null;
                }

                return String.join(",", this.productionSystemArguments);
            }
            case BashRefinementPluginDescriptor.CONFIGURATION_ENTRY_USERNAME -> {
                return this.userName;
            }
            case BashRefinementPluginDescriptor.CONFIGURATION_ENTRY_DEFAULT_PRIVATE_KEY -> {
                return this.defaultPrivateKey;
            }
            case BashRefinementPluginDescriptor.CONFIGURATION_ENTRY_OUTPUT_PROPERTY_NAME -> {
                return this.outputPropertyName;
            }
            case BashRefinementPluginDescriptor.CONFIGURATION_ENTRY_OUTPUT_PROPERTY_TYPE -> {
                if (outputPropertyType == null) {
                    return null;
                }

                return outputPropertyType.name();
            }
            // anything but "true" (ignore case) is considered false (even null is considered false).
            case BashRefinementPluginDescriptor.CONFIGURATION_ENTRY_IGNORE_MISSING_PROPERTIES -> {
                return String.valueOf(this.ignoreMissingProperties);
            }
            default -> {
                LOGGER.warn("Trying to get an unknown configuration entry '{}'!", name);
                return null;
            }
        }
    }

    @Override
    public InstanceModel refineModel(InstanceModel instanceModel, ProductionSystem productionSystem) throws
            MissingProductionSystemPropertyException, MissingConfigurationEntryException {
        Collection<Compute> ubuntuNodes = validateInputs(instanceModel, productionSystem);
        String script = this.script;

        if (productionSystemArguments != null && !productionSystemArguments.isEmpty()) {
            // be sure to maintain correct ordering
            List<String> args = productionSystemArguments.stream()
                    .map(pName -> productionSystem.getProperties().get(pName))
                    .toList();

            script = "%s %s".formatted(script, String.join(" ", args));
        }

        String output = null;
        Object parsedOutput;

        for (Compute vmComponent : ubuntuNodes) {
            // we know the following is not null (see validation)
            String hostname = vmComponent.getProperty(Compute.PUBLIC_ADDRESS).orElse(null);
            String privateKey = Utils.readPrivateKey(vmComponent, this.defaultPrivateKey, getIdentifier());
            VirtualMachine virtualMachine = new VirtualMachine(hostname, null, userName, privateKey);

            try {
                virtualMachine.connect();
                LOGGER.info("Connected to vm (%s).\n".formatted(virtualMachine));
                script = virtualMachine.replaceHome(script);
                LOGGER.info("Replaced root with the current path.\n");
                output = virtualMachine.execCommand(script);
                LOGGER.info("Executed command: {}", script);
                LOGGER.info("The resulting output was: {}", output);
                parsedOutput = parseOutput(output);
                Edmm.addPropertyAssignments(instanceModel.getDeploymentModel().getGraph(), vmComponent.getEntity().getId(),
                        Map.of(this.outputPropertyName, parsedOutput));
            } catch (NumberFormatException ne) {
                throw new WrongOutputTypeException(output, script, vmComponent.getId(), outputPropertyType);
            } catch (Exception e) {
                throw new BashCommandExecutionException(script, vmComponent.getId(), hostname, userName);
            } finally {
                virtualMachine.disconnect();
            }
        }

        instanceModel.reCreateDeploymentModel();

        return instanceModel;
    }

    public Collection<Compute> validateInputs(InstanceModel model, ProductionSystem productionSystem) {
        if (script == null || script.isEmpty()) {
            throw new MissingConfigurationEntryException(getIdentifier(), BashFixingPluginDescriptor.CONFIGURATION_ENTRY_SCRIPT);
        }

        if (userName == null || userName.isEmpty()) {
            throw new MissingConfigurationEntryException(getIdentifier(), BashFixingPluginDescriptor.CONFIGURATION_ENTRY_USERNAME);
        }

        if (outputPropertyName == null || outputPropertyName.isEmpty()) {
            throw new MissingConfigurationEntryException(getIdentifier(), BashRefinementPluginDescriptor.CONFIGURATION_ENTRY_OUTPUT_PROPERTY_NAME);
        }

        if (outputPropertyType == null) {
            throw new MissingConfigurationEntryException(getIdentifier(), BashRefinementPluginDescriptor.CONFIGURATION_ENTRY_OUTPUT_PROPERTY_TYPE);
        }

        if (productionSystemArguments != null && !productionSystemArguments.isEmpty()) {

            String missingCRParam = productionSystemArguments.stream()
                    .filter(pName -> productionSystem.getProperties().keySet().stream().noneMatch(p -> p.equals(pName)))
                    .findFirst().orElse(null);

            if (missingCRParam != null) {
                throw new MissingProductionSystemPropertyException(productionSystem, missingCRParam);
            }
        }

        Collection<Compute> allUbuntuNodes = Edmm.getAllComponentsOfType(model.getDeploymentModel(), Compute.class).stream()
                .filter(node -> node.getProperty(Compute.OS_FAMILY).stream().allMatch("linux"::equalsIgnoreCase))
                .filter(node -> node.getProperty(Compute.MACHINE_IMAGE).stream().allMatch("ubuntu"::equalsIgnoreCase))
                .toList();
        Collection<Compute> result = new ArrayList<>();
        String address;
        String pKeyPath;
        boolean addressExists;
        boolean pKeyExists;

        for (Compute node : allUbuntuNodes) {
            address = node.getProperty(Compute.PUBLIC_ADDRESS).orElse(null);
            addressExists = address != null && !address.isEmpty();

            if (defaultPrivateKey == null || defaultPrivateKey.isEmpty()) {
                pKeyPath = node.getProperty(Compute.PRIVATE_KEY_PATH).orElse(null);
                pKeyExists = pKeyPath != null && !pKeyPath.isEmpty();
            } else {
                pKeyExists = true;
            }

            // all information are provided
            if (addressExists && pKeyExists) {
                result.add(node);
            } else if (!ignoreMissingProperties) {
                String missingPropertyName;

                if (!addressExists) {
                    missingPropertyName = Compute.PUBLIC_ADDRESS.getName();
                } else {
                    missingPropertyName = Compute.PRIVATE_KEY_PATH.getName();
                }

                throw new MalformedInstanceModelException(node.getId(), missingPropertyName,
                        "VM component (id: %s) is missing a property (name: %s) necessary to establish ssh connection to it."
                                .formatted(node.getId(), missingPropertyName));
            }
            // if we are allowed to ignore that the node has missing properties, we don't throw an exception, but we don't add
            // the node to the final list. The final list of Compute nodes contains only nodes to which the plugin should be
            // able to connect.
        }

        return result;
    }
}
