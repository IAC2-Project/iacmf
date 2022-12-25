package org.iac2.entity.plugin;

import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.iac2.entity.compliancejob.execution.ExecutionEntity;

@Entity
@Data
@NoArgsConstructor
public class PluginUsageInstanceEntity extends PluginUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "plugin_usage_id")
    private PluginUsageEntity pluginUsage;

    @ManyToOne
    @JoinColumn(name = "execution_id")
    private ExecutionEntity execution;

    public PluginUsageInstanceEntity(PluginUsageEntity pluginUsage) {
        super(pluginUsage.getPluginIdentifier(), new ArrayList<>());
        this.pluginUsage = pluginUsage;
    }

    public PluginUsageInstanceEntity addPluginConfiguration(PluginConfigurationEntity entity) {
        entity.setPluginUsageInstance(this);
        this.getPluginConfiguration().add(entity);

        return this;
    }
}
