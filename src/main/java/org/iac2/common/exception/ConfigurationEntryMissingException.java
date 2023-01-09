package org.iac2.common.exception;

import lombok.Getter;

@Getter
public class ConfigurationEntryMissingException extends IacmfException {
    private final String pluginId;
    private final String entryName;

    public ConfigurationEntryMissingException(String pluginId, String entryName) {
        super("The plugin with the id: '%s' is missing a required configuration entry: '%s'".formatted(pluginId, entryName));
        this.pluginId = pluginId;
        this.entryName = entryName;
    }
}
