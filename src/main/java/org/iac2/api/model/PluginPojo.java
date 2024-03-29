package org.iac2.api.model;

import java.util.Collection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iac2.common.model.PluginConfigurationEntryDescriptor;
import org.iac2.common.model.PluginType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PluginPojo {
    String identifier;

    String description;

    PluginType pluginType;

    Collection<PluginConfigurationEntryDescriptor> configurationEntryNames;
}
