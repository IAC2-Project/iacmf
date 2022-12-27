package org.iac2.entity.plugin;

import java.util.List;

import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PluginUsage {
    @NotNull
    private String pluginIdentifier;
    @OneToMany(mappedBy = "pluginUsage")
    private List<PluginConfigurationEntity> pluginConfiguration;
}
