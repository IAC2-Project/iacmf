package org.iac2.entity.architecturereconstruction;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.iac2.entity.configuration.PluginConfigurationEntity;
import org.iac2.entity.util.StringListConverter;

@Entity
@Data
@NoArgsConstructor
public class ModelEnhancementStrategyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Convert(converter = StringListConverter.class)
    private List<String> pluginIdList;

    @OneToMany(mappedBy = "enhancementStrategy")
    private List<PluginConfigurationEntity> pluginConfiguration;

    public ModelEnhancementStrategyEntity(List<String> pluginIdList) {
        this.pluginIdList = pluginIdList;
        this.pluginConfiguration = new ArrayList<>();
    }

    public List<PluginConfigurationEntity> getConfigurationOfPlugin(String pluginId) {
        return this.pluginConfiguration.stream().filter(c -> c.getPluginIdentifier().equals(pluginId)).toList();
    }
}
