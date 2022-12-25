package org.iac2.common;

import java.util.Collection;

import org.iac2.common.exception.PluginNotFoundException;

public interface PluginFactory {

    Plugin createPlugin(String identifier) throws PluginNotFoundException;

    Collection<String> getAllPluginIdentifiers();

    PluginDescriptor describePlugin(String identifier);

    boolean pluginExists(String identifier);
}
