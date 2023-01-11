package org.iac2.common.exception;

import lombok.Getter;

@Getter
public abstract class ConfigurationEntryException extends IacmfException {
    private final String pluginId;
    private final String entryName;

    public ConfigurationEntryException(String pluginId, String entryName, String message) {
        super(message);
        this.pluginId = pluginId;
        this.entryName = entryName;
    }
}
