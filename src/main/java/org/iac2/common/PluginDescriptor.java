package org.iac2.common;

import java.util.Collection;

import org.iac2.common.model.PluginConfigurationEntryDescriptor;

public interface PluginDescriptor {
    String getIdentifier();

    String getDescription();

    /***
     * Reports the required set of configuration entry names needed for the operation of the plugin.
     *
     * @return the set of configuration entry names needed for the operation of the plugin.
     */
    Collection<PluginConfigurationEntryDescriptor> getConfigurationEntryDescriptors();

    Plugin createPlugin();
}
