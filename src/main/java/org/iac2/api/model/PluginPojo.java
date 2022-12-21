package org.iac2.api.model;

import java.util.Collection;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iac2.common.model.PluginType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PluginPojo {
    String identifier;

    PluginType pluginType;

    Collection<String> requiredConfigurationEntryNames;

    Map<String, String> configurationEntries;
}
