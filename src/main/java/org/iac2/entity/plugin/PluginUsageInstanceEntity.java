package org.iac2.entity.plugin;

import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iac2.entity.compliancejob.execution.ExecutionEntity;

@Entity
@Data
@NoArgsConstructor
public class PluginUsageInstanceEntity extends PluginUsage {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "plugin_usage_id")
    private PluginUsageEntity pluginUsage;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "execution_id")
    private ExecutionEntity execution;

    public PluginUsageInstanceEntity(PluginUsageEntity pluginUsage, ExecutionEntity execution) {
        super(pluginUsage.getPluginIdentifier(), new ArrayList<>());
        this.pluginUsage = pluginUsage;
        this.execution = execution;
        this.execution.getPluginUsageInstances().add(this);
    }
}
