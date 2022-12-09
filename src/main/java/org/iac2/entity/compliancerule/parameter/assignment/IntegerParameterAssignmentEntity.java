package org.iac2.entity.compliancerule.parameter.assignment;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.compliancerule.parameter.ComplianceRuleParameterEntity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = IntegerParameterAssignmentEntity.TYPE_ID)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class IntegerParameterAssignmentEntity extends ComplianceRuleParameterAssignmentEntity {
    public static final String TYPE_ID = "1";
    private Integer intValue;

    public IntegerParameterAssignmentEntity(ComplianceRuleParameterEntity parameter,
                                            ComplianceJobEntity complianceJob,
                                            int value) {
        super(parameter, complianceJob);
        this.intValue = value;
    }
}
