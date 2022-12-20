package org.iac2.entity.compliancerule.parameter;

import java.util.List;
import java.util.Optional;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.util.StringListConverter;

@Data
@NoArgsConstructor
@Entity
public class ComplianceRuleParameterAssignmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    protected String value;

    @ManyToOne
    @JoinColumn(nullable = false)
    protected ComplianceRuleParameterEntity parameter;

    @ManyToOne
    @JoinColumn(nullable = false)
    protected ComplianceJobEntity complianceJob;

    public ComplianceRuleParameterAssignmentEntity(ComplianceRuleParameterEntity parameter, ComplianceJobEntity complianceJob, String value) {
        this.parameter = parameter;
        this.complianceJob = complianceJob;
        this.value = value;
    }

    public ComplianceRuleParameterAssignmentEntity(ComplianceRuleParameterEntity parameter, ComplianceJobEntity complianceJob, Integer value) {
        this(parameter, complianceJob, String.valueOf(value));
        assert parameter.getType() == ParameterType.INT;
    }

    public ComplianceRuleParameterAssignmentEntity(ComplianceRuleParameterEntity parameter, ComplianceJobEntity complianceJob, Boolean value) {
        this(parameter, complianceJob, String.valueOf(value));
        assert parameter.getType() == ParameterType.BOOLEAN;
    }

    public ComplianceRuleParameterAssignmentEntity(ComplianceRuleParameterEntity parameter, ComplianceJobEntity complianceJob, Double value) {
        this(parameter, complianceJob, String.valueOf(value));
        assert parameter.getType() == ParameterType.DECIMAL;
    }

    public ComplianceRuleParameterAssignmentEntity(ComplianceRuleParameterEntity parameter, ComplianceJobEntity complianceJob, List<String> value) {
        this(parameter, complianceJob, (new StringListConverter()).convertToDatabaseColumn(value));
        assert parameter.getType() == ParameterType.STRING_LIST;
    }

    public ParameterType getType() {
        return this.parameter.getType();
    }

    public String getName() {
        return this.parameter.getName();
    }

    public Optional<Integer> getIntegerValue() {
        if (this.parameter.getType() == ParameterType.INT) {
            return Optional.of(Integer.valueOf(this.value));
        }

        return Optional.empty();
    }

    public Optional<Double> getDoubleValue() {
        if (this.parameter.getType() == ParameterType.DECIMAL) {
            return Optional.of(Double.valueOf(this.value));
        }

        return Optional.empty();
    }

    public Optional<Boolean> getBooleanValue() {
        if (this.parameter.getType() == ParameterType.BOOLEAN) {
            return Optional.of(Boolean.valueOf(this.value));
        }

        return Optional.empty();
    }

    public Optional<List<String>> getStringListValue() {
        if (this.parameter.getType() == ParameterType.STRING_LIST) {
            return Optional.of((new StringListConverter()).convertToEntityAttribute(this.value));
        }

        return Optional.empty();
    }
}
