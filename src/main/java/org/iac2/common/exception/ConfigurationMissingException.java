package org.iac2.common.exception;

import lombok.Getter;

@Getter
public class ConfigurationMissingException extends IacmfException {
    private final String pluginId;
    private final String configurationEntryName;

    public ConfigurationMissingException(String pluginId, String configurationEntryName) {
        super(String.format("Plugin (plugin-id: %s) is missing a required configuration entry (entry-name: %s)",
                pluginId, configurationEntryName));
        this.pluginId = pluginId;
        this.configurationEntryName = configurationEntryName;
    }
}
