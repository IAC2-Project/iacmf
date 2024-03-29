package org.iac2.entity.compliancerule.parameter;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.iac2.common.model.compliancerule.ParameterType;
import org.iac2.entity.compliancejob.ComplianceRuleConfigurationEntity;
import org.iac2.entity.util.StringListConverter;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class ComplianceRuleParameterAssignmentEntity {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    protected String value;

    // unidirectional
    @ManyToOne
    @JoinColumn(nullable = false)
    protected ComplianceRuleParameterEntity parameter;

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @ManyToOne
    @JoinColumn(nullable = false)
    protected ComplianceRuleConfigurationEntity complianceRuleConfiguration;

    public ComplianceRuleParameterAssignmentEntity(ComplianceRuleParameterEntity parameter, ComplianceRuleConfigurationEntity complianceRuleConfiguration, String value) {
        this.parameter = parameter;
        this.complianceRuleConfiguration = complianceRuleConfiguration;
        this.complianceRuleConfiguration.getComplianceRuleParameterAssignments().add(this);
        this.value = value;
    }

    public ComplianceRuleParameterAssignmentEntity(ComplianceRuleParameterEntity parameter, ComplianceRuleConfigurationEntity complianceRuleConfiguration, Integer value) {
        this(parameter, complianceRuleConfiguration, String.valueOf(value));
        assert parameter.getType() == ParameterType.INT;
    }

    public ComplianceRuleParameterAssignmentEntity(ComplianceRuleParameterEntity parameter, ComplianceRuleConfigurationEntity complianceRuleConfiguration, Boolean value) {
        this(parameter, complianceRuleConfiguration, String.valueOf(value));
        assert parameter.getType() == ParameterType.BOOLEAN;
    }

    public ComplianceRuleParameterAssignmentEntity(ComplianceRuleParameterEntity parameter, ComplianceRuleConfigurationEntity complianceRuleConfiguration, Double value) {
        this(parameter, complianceRuleConfiguration, String.valueOf(value));
        assert parameter.getType() == ParameterType.DECIMAL;
    }

    public ComplianceRuleParameterAssignmentEntity(ComplianceRuleParameterEntity parameter, ComplianceRuleConfigurationEntity complianceRuleConfiguration, List<String> value) {
        this(parameter, complianceRuleConfiguration, (new StringListConverter()).convertToDatabaseColumn(value));
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComplianceRuleParameterAssignmentEntity that = (ComplianceRuleParameterAssignmentEntity) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
