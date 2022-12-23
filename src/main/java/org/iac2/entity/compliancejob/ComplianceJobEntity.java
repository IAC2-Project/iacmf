package org.iac2.entity.compliancejob;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.iac2.entity.compliancejob.execution.ExecutionEntity;
import org.iac2.entity.compliancejob.issue.IssueFixingConfigurationEntity;
import org.iac2.entity.compliancejob.trigger.TriggerEntity;
import org.iac2.entity.plugin.PluginUsageEntity;
import org.iac2.entity.productionsystem.ProductionSystemEntity;

@Entity
@Data
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.INTEGER)
public class ComplianceJobEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String description;

    @NotNull
    private String modelCheckingPluginId;

    @OneToMany(mappedBy = "complianceJob")
    private List<IssueFixingConfigurationEntity> issueFixingConfigurations;

    @ManyToMany
    @JoinTable(
            name = "job_trigger",
            joinColumns = @JoinColumn(name = "compliance_job_id"),
            inverseJoinColumns = @JoinColumn(name = "trigger_id"))
    private List<TriggerEntity> triggers;

    @OneToMany(mappedBy = "complianceJob")
    private List<ComplianceRuleConfigurationEntity> complianceRuleConfigurations;

    @OneToMany(mappedBy = "complianceJob")
    private List<ExecutionEntity> executions;

    @OneToMany(mappedBy = "complianceJobRefinement")
    private List<PluginUsageEntity> modelEnhancementStrategy;

    @OneToOne
    @JoinColumn(name = "checking_plugin_usage_id", nullable = false)
    private PluginUsageEntity checkingPluginUsage;

    @ManyToOne
    @JoinColumn(name = "production_system_id", nullable = false)
    private ProductionSystemEntity productionSystem;

    public ComplianceJobEntity(String description,
                               String modelCheckingPluginId,
                               ProductionSystemEntity productionSystem,
                               List<ComplianceRuleConfigurationEntity> complianceRuleConfigurations,
                               List<PluginUsageEntity> modelEnhancementStrategy,
                               PluginUsageEntity checkingPluginUsage,
                               List<IssueFixingConfigurationEntity> issueFixingConfigurations,
                               List<TriggerEntity> triggers) {
        this.modelCheckingPluginId = modelCheckingPluginId;
        this.productionSystem = productionSystem;
        this.triggers = triggers;
        this.description = description;
        this.executions = new ArrayList<>();
        this.complianceRuleConfigurations = complianceRuleConfigurations;
        this.issueFixingConfigurations = issueFixingConfigurations;
        this.modelEnhancementStrategy = modelEnhancementStrategy;
        this.checkingPluginUsage = checkingPluginUsage;
    }
}
