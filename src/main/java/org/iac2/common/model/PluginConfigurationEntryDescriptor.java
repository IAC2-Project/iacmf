package org.iac2.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PluginConfigurationEntryDescriptor {
    String name;
    PluginConfigurationEntryType type;
    Boolean isRequired;
    String description;
}
