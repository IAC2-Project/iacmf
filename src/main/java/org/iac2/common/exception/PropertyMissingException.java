package org.iac2.common.exception;

public class PropertyMissingException extends IacmfException {
    private String pluginId;
    private String componentIdentifier;
    private String propertyName;

    public PropertyMissingException(String pluginId, String componentIdentifier, String propertyName) {
        super(String.format("The plugin (id: %s) is trying to access a missing property (name: %s)" +
                " in the component (id: %s) of the reconstructed instance model.", pluginId, propertyName, componentIdentifier));
        this.pluginId = pluginId;
        this.componentIdentifier = componentIdentifier;
        this.propertyName = propertyName;
    }
}
