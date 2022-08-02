package org.iac2.entity.compliancerule.parameter.assignment;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.compliancerule.parameter.ParameterEntity;
import org.iac2.entity.compliancerule.parameter.assignment.ParameterAssignmentEntity;

@Entity
@DiscriminatorValue(value = "1")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class IntegerParameterAssignmentEntity extends ParameterAssignmentEntity {
    private Integer intValue;

    public IntegerParameterAssignmentEntity(ParameterEntity parameter,
                                            ComplianceJobEntity complianceJob,
                                            int value) {
        super(parameter, complianceJob);
        this.intValue = value;
    }
}
