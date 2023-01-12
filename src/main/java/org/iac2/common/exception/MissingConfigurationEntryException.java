package org.iac2.common.exception;

import lombok.Getter;

@Getter
public class MissingConfigurationEntryException extends ConfigurationEntryException {

    public MissingConfigurationEntryException(String pluginId, String entryName) {
        super(pluginId, entryName, "The plugin with the id: '%s' is missing a required configuration entry: '%s'".formatted(pluginId, entryName));
    }
}
