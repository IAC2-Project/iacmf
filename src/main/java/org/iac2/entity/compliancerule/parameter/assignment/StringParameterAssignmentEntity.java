package org.iac2.entity.compliancerule.parameter.assignment;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.compliancerule.parameter.ParameterEntity;

@Entity
@DiscriminatorValue(value = "2")
@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
public class StringParameterAssignmentEntity extends ParameterAssignmentEntity {
    private String stringValue;

    public StringParameterAssignmentEntity(ParameterEntity parameter,
                                           ComplianceJobEntity complianceJob,
                                           String value) {
        super(parameter, complianceJob);
        this.stringValue = value;
    }
}
