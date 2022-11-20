package org.iac2.entity.compliancerule.parameter.assignment;

import java.util.List;

import javax.persistence.Convert;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.compliancerule.parameter.ComplianceRuleParameterEntity;
import org.iac2.entity.util.StringListConverter;

@Entity
@DiscriminatorValue(value = "4")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class StringListParameterAssignmentEntity extends ComplianceRuleParameterAssignmentEntity {

    @Convert(converter = StringListConverter.class)
    private List<String> value;

    public StringListParameterAssignmentEntity(ComplianceRuleParameterEntity parameter,
                                               ComplianceJobEntity complianceJob,
                                               List<String> value) {
        super(parameter, complianceJob);
        this.value = value;
    }
}
