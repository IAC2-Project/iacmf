package org.iac2.entity.compliancerule.parameter.assignment;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.compliancerule.parameter.ComplianceRuleParameterEntity;

@Entity
@DiscriminatorValue(value = "2")
@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
public class StringParameterAssignmentEntity extends ComplianceRuleParameterAssignmentEntity {
    private String stringValue;

    public StringParameterAssignmentEntity(ComplianceRuleParameterEntity parameter,
                                           ComplianceJobEntity complianceJob,
                                           String value) {
        super(parameter, complianceJob);
        this.stringValue = value;
    }
}
