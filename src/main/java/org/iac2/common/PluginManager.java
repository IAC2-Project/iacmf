package org.iac2.common;

import java.util.Collection;

import org.iac2.common.exception.PluginNotFoundException;

public interface PluginManager {

    Plugin getPlugin(String identifier) throws PluginNotFoundException;

    Collection<? extends Plugin> getAll();

    boolean pluginExists(String identifier);
}
