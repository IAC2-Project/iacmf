package org.iac2.entity.compliancejob;

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

import lombok.Data;
import lombok.NoArgsConstructor;
import org.iac2.entity.compliancejob.execution.ExecutionEntity;
import org.iac2.entity.compliancejob.trigger.TriggerEntity;
import org.iac2.entity.compliancerule.ComplianceRuleEntity;
import org.iac2.entity.compliancerule.parameter.assignment.ParameterAssignmentEntity;
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

    @ManyToOne
    @JoinColumn(name = "compliance_rule_id", nullable = false)
    private ComplianceRuleEntity complianceRule;


    @ManyToMany
    @JoinTable(
            name = "job_trigger",
            joinColumns = @JoinColumn(name = "compliance_job_id"),
            inverseJoinColumns = @JoinColumn(name = "trigger_id"))
    private List<TriggerEntity> triggers;


    @OneToMany(mappedBy = "complianceJob")
    private List<ParameterAssignmentEntity> parameterAssignments;

    @OneToMany(mappedBy = "complianceJob")
    private List<ExecutionEntity> executions;

    @ManyToOne
    @JoinColumn(name = "production_system_id", nullable = false)
    private ProductionSystemEntity productionSystem;

    public ComplianceJobEntity(String description,
                               ProductionSystemEntity productionSystem,
                               ComplianceRuleEntity complianceRule,
                               List<TriggerEntity> triggers
                               ) {
        this.complianceRule = complianceRule;
        this.productionSystem = productionSystem;
        this.triggers = triggers;
        this.description = description;
    }


}
