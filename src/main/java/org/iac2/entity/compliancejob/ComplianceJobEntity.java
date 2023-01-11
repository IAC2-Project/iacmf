package org.iac2.entity.compliancejob;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
public class ComplianceJobEntity {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String name;

    private String description;

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
    private List<PluginUsageEntity> modelRefinementStrategy;

    @OneToOne
    @JoinColumn(name = "checking_plugin_usage_id", nullable = false)
    private PluginUsageEntity checkingPluginUsage;

    @ManyToOne
    @JoinColumn(name = "production_system_id", nullable = false)
    private ProductionSystemEntity productionSystem;

    public ComplianceJobEntity(String name,
                               String description,
                               @NotNull ProductionSystemEntity productionSystem,
                               @NotNull PluginUsageEntity checkingPluginUsage) {
        this.name = name;
        this.productionSystem = productionSystem;
        this.checkingPluginUsage = checkingPluginUsage;
        this.checkingPluginUsage.setComplianceJobChecking(this);
        this.description = description;
        this.executions = new ArrayList<>();
        this.complianceRuleConfigurations = new ArrayList<>();
        this.issueFixingConfigurations = new ArrayList<>();
        this.modelRefinementStrategy = new ArrayList<>();
        this.triggers = new ArrayList<>();
    }

    public ComplianceJobEntity addTrigger(TriggerEntity trigger) {
        trigger.getComplianceJobs().add(this);
        this.triggers.add(trigger);

        return this;
    }
}
