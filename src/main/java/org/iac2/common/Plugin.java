package org.iac2.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface Plugin {
    String getIdentifier();

    /***
     * Reports the required set of configuration entry names needed for the operation of the plugin.
     * @return the set of configuration entry names needed for the operation of the plugin.
     */
    Collection<String> getRequiredConfigurationEntryNames();

    void setConfigurationEntry(String inputName, String inputValue);

    String getConfigurationEntry(String name);

    default Map<String, String> getConfigurationEntries() {
        Map<String, String> result = new HashMap<>();
        getRequiredConfigurationEntryNames().forEach(name -> result.put(name, getConfigurationEntry(name)));
        
        return result;
    }
}
