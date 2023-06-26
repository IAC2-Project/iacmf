package org.iac2.entity.plugin;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iac2.entity.compliancejob.execution.ExecutionEntity;

@Entity
@Data
@NoArgsConstructor
public class PluginUsageInstanceEntity {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String pluginIdentifier;

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @OneToMany(mappedBy = "pluginUsageInstance")
    private List<PluginConfigurationEntity> pluginConfiguration;

    //unidirectional
    @ManyToOne
    @JoinColumn(name = "plugin_usage_id")
    private PluginUsageEntity pluginUsage;

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @ManyToOne
    @JoinColumn(name = "execution_id")
    private ExecutionEntity execution;

    public PluginUsageInstanceEntity(PluginUsageEntity pluginUsage, ExecutionEntity execution) {
        this.pluginIdentifier = pluginUsage.getPluginIdentifier();
        this.pluginConfiguration = new ArrayList<>();
        this.pluginUsage = pluginUsage;
        this.execution = execution;
        this.execution.getPluginUsageInstances().add(this);
    }
}
