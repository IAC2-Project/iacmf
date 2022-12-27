package org.iac2.common;

import java.util.Collection;

public interface PluginDescriptor {
    String getIdentifier();

    /***
     * Reports the required set of configuration entry names needed for the operation of the plugin.
     * @return the set of configuration entry names needed for the operation of the plugin.
     */
    Collection<String> getRequiredConfigurationEntryNames();

    Plugin createPlugin();
}
