package org.iac2.common.exception;

import lombok.Getter;

@Getter
public class PluginNotFoundException extends IacmfException{
    private final String pluginId;
    private final PluginType pluginType;

    public PluginNotFoundException(String pluginId, PluginType pluginType) {
        super("A plugin with the id: '" + pluginId + "' and type '" + pluginType.name() + "' cannot be found!");
        this.pluginId = pluginId;
        this.pluginType = pluginType;
    }
}
