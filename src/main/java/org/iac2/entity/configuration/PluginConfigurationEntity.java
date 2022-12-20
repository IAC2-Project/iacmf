package org.iac2.entity.configuration;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.iac2.entity.architecturereconstruction.ModelEnhancementStrategyEntity;
import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.productionsystem.ProductionSystemEntity;

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

    /**
     * This configuration entity is associated with a plugin of a model enhancement strategy.
     */
    @ManyToOne
    @JoinColumn(name = "enhancement_strategy_id")
    private ModelEnhancementStrategyEntity enhancementStrategy;

    /**
     * This configuration entity is associated with the fixing plugin or the checking plugin of a compliance job.
     */
    @ManyToOne
    @JoinColumn(name = "compliance_job_id")
    private ComplianceJobEntity complianceJob;

    /**
     * This configuration entity is associated with a model creation plugin of a production system.
     */
    @ManyToOne
    @JoinColumn(name = "production_system_id")
    private ProductionSystemEntity productionSystem;

    private PluginConfigurationEntity(String pluginIdentifier, String key, String value) {
        this.pluginIdentifier = pluginIdentifier;
        this.key = key;
        this.value = value;
    }

    public static PluginConfigurationEntity forModelEnhancementStrategy(String pluginIdentifier,
                                                                      String key,
                                                                      String value,
                                                                      ModelEnhancementStrategyEntity modelEnhancementStrategy) {
        PluginConfigurationEntity result = new PluginConfigurationEntity(pluginIdentifier, key, value);
        result.enhancementStrategy = modelEnhancementStrategy;

        return result;
    }

    public static PluginConfigurationEntity forComplianceJob(String pluginIdentifier,
                                                             String key,
                                                             String value,
                                                             ComplianceJobEntity complianceJob) {
        PluginConfigurationEntity result = new PluginConfigurationEntity(pluginIdentifier, key, value);
        result.complianceJob = complianceJob;

        return result;
    }

    public static PluginConfigurationEntity forProductionSystem(String pluginIdentifier,
                                                                String key,
                                                                String value,
                                                                ProductionSystemEntity productionSystem) {
        PluginConfigurationEntity result = new PluginConfigurationEntity(pluginIdentifier, key, value);
        result.productionSystem = productionSystem;

        return result;
    }
}
