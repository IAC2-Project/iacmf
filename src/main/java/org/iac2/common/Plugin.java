package org.iac2.common;

import java.util.HashMap;
import java.util.Map;

public interface Plugin {
    PluginDescriptor getDescriptor();

    default String getIdentifier() {
        return getDescriptor().getIdentifier();
    }

    void setConfigurationEntry(String inputName, String inputValue);

    String getConfigurationEntry(String name);

    default Map<String, String> getConfigurationEntries() {
        Map<String, String> result = new HashMap<>();
        getDescriptor().getConfigurationEntryDescriptors()
                .forEach(descriptor -> result.put(descriptor.getName(), getConfigurationEntry(descriptor.getDescription())));

        return result;
    }
}
