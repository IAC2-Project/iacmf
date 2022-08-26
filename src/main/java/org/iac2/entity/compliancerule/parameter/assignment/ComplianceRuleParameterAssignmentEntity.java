package org.iac2.entity.compliancerule.parameter.assignment;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.compliancerule.parameter.ComplianceRuleParameterEntity;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.INTEGER)
public abstract class ComplianceRuleParameterAssignmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    protected ComplianceRuleParameterEntity parameter;

    @ManyToOne
    @JoinColumn(nullable = false)
    protected ComplianceJobEntity complianceJob;

    public ComplianceRuleParameterAssignmentEntity(ComplianceRuleParameterEntity parameter, ComplianceJobEntity complianceJob) {
        this.parameter = parameter;
        this.complianceJob = complianceJob;
    }
}
