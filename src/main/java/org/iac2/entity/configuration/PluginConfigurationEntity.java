package org.iac2.entity.configuration;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
@Data
@NoArgsConstructor
public class PluginConfigurationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String pluginIdentifier;

    @NotNull
    private String key;

    @NotNull
    private String value;

    public PluginConfigurationEntity(String pluginIdentifier, String key, String value) {
        this.pluginIdentifier = pluginIdentifier;
        this.key = key;
        this.value = value;
    }
}
