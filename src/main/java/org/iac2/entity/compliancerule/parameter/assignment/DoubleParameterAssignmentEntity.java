package org.iac2.entity.compliancerule.parameter.assignment;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.compliancerule.parameter.ComplianceRuleParameterEntity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = DoubleParameterAssignmentEntity.TYPE_ID)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class DoubleParameterAssignmentEntity extends ComplianceRuleParameterAssignmentEntity {
    public static final String TYPE_ID = "3";
    private Double doubleValue;

    public DoubleParameterAssignmentEntity(ComplianceRuleParameterEntity parameter,
                                           ComplianceJobEntity complianceJob,
                                           double value) {
        super(parameter, complianceJob);
        this.doubleValue = value;
    }

}
