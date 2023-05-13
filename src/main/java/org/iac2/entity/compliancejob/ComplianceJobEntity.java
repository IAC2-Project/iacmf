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
import javax.persistence.OrderBy;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iac2.entity.compliancejob.execution.ExecutionEntity;
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

    @ManyToMany
    @JoinTable(
            name = "job_trigger",
            joinColumns = @JoinColumn(name = "compliance_job_id"),
            inverseJoinColumns = @JoinColumn(name = "trigger_id"))
    private List<TriggerEntity> triggers;

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @OneToMany(mappedBy = "complianceJob")
    private List<ComplianceRuleConfigurationEntity> complianceRuleConfigurations;

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @OneToMany(mappedBy = "complianceJob")
    private List<ExecutionEntity> executions;

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @OneToMany(mappedBy = "complianceJobRefinement")
    @OrderBy("refinementPluginIndexInComplianceJob")
    private List<PluginUsageEntity> modelRefinementStrategy;

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @OneToOne
    @JoinColumn(name = "checking_plugin_usage_id", nullable = false)
    private PluginUsageEntity checkingPluginUsage;

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @OneToOne
    @JoinColumn(name = "reporting_plugin_usage_id", nullable = false)
    private PluginUsageEntity reportingPluginUsage;

    // unidirectional
    @ManyToOne
    @JoinColumn(name = "production_system_id", nullable = false)
    private ProductionSystemEntity productionSystem;

    public ComplianceJobEntity(String name,
                               String description,
                               @NotNull ProductionSystemEntity productionSystem,
                               @NotNull PluginUsageEntity checkingPluginUsage,
                               @NotNull PluginUsageEntity reportingPluginUsage) {
        this.name = name;
        this.productionSystem = productionSystem;
        this.checkingPluginUsage = checkingPluginUsage;
        this.reportingPluginUsage = reportingPluginUsage;
        this.checkingPluginUsage.setComplianceJobChecking(this);
        this.description = description;
        this.executions = new ArrayList<>();
        this.complianceRuleConfigurations = new ArrayList<>();
        this.modelRefinementStrategy = new ArrayList<>();
        this.triggers = new ArrayList<>();
    }

    public ComplianceJobEntity addTrigger(TriggerEntity trigger) {
        trigger.getComplianceJobs().add(this);
        this.triggers.add(trigger);

        return this;
    }
}
