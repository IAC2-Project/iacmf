package org.iac2.entity.plugin;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.iac2.entity.compliancejob.execution.ExecutionEntity;

@Entity
@Data
@NoArgsConstructor
public class PluginUsageInstanceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "plugin_usage_id")
    private PluginUsageEntity pluginUsage;

    @OneToMany(mappedBy = "pluginUsageInstance")
    private List<PluginConfigurationEntity> pluginConfiguration;

    @ManyToOne
    @JoinColumn(name = "execution_id")
    private ExecutionEntity execution;

    public PluginUsageInstanceEntity(PluginUsageEntity pluginUsage, List<PluginConfigurationEntity> pluginConfiguration, ExecutionEntity execution) {
        this.pluginUsage = pluginUsage;
        this.pluginConfiguration = pluginConfiguration;
        this.execution = execution;
    }
}
