package org.iac2.common.exception;

public class MalformedConfigurationEntryException extends ConfigurationEntryException {
    public MalformedConfigurationEntryException(String pluginId, String entryName) {
        super(pluginId, entryName, "The configuration entry (name: %s) for the plugin (id: %s) is malformed".formatted(entryName, pluginId));
    }
}
