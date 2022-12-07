package org.iac2.entity.configuration;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.iac2.entity.KVEntity;

@Entity
@DiscriminatorValue(value = PluginConfigurationPropertyEntity.TYPE_ID)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PluginConfigurationPropertyEntity extends KVEntity {
    public static final String TYPE_ID = "3";

    @ManyToOne
    @JoinColumn(name = "plugin_configuration_id")
    private PluginConfigurationEntity pluginConfiguration;

    public PluginConfigurationPropertyEntity(String key, String value, PluginConfigurationEntity pluginConfiguration) {
        super(key, value);
        this.pluginConfiguration = pluginConfiguration;
    }
}
