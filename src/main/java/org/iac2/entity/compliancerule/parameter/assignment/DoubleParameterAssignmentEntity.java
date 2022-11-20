package org.iac2.entity.compliancerule.parameter.assignment;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.compliancerule.parameter.ComplianceRuleParameterEntity;

@Entity
@DiscriminatorValue(value = "3")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class DoubleParameterAssignmentEntity extends ComplianceRuleParameterAssignmentEntity {
    private Double doubleValue;

    public DoubleParameterAssignmentEntity(ComplianceRuleParameterEntity parameter,
                                            ComplianceJobEntity complianceJob,
                                            double value) {
        super(parameter, complianceJob);
        this.doubleValue = value;
    }

}
