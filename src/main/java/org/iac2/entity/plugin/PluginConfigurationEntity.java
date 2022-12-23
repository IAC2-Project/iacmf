package org.iac2.entity.plugin;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class PluginConfigurationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String key;

    @NotNull
    private String value;

    @ManyToOne
    @JoinColumn(name = "plugin_usage_id")
    private PluginUsageEntity pluginUsage;

    @ManyToOne
    @JoinColumn(name = "plugin_usage_instance_id")
    private PluginUsageInstanceEntity pluginUsageInstance;

    private PluginConfigurationEntity(String key, String value, PluginUsageEntity pluginUsage, PluginUsageInstanceEntity pluginUsageInstance) {
        this.key = key;
        this.value = value;
        this.pluginUsage = pluginUsage;
        this.pluginUsageInstance = pluginUsageInstance;
    }

    public PluginConfigurationEntity(String key, String value, PluginUsageEntity pluginUsage) {
        this(key, value, pluginUsage, null);
    }

    public PluginConfigurationEntity(String key, String value, PluginUsageInstanceEntity pluginUsageInstance) {
        this(key, value, null, pluginUsageInstance);
    }
}
