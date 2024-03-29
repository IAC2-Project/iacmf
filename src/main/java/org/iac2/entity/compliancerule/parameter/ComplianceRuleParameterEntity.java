package org.iac2.entity.compliancerule.parameter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iac2.common.model.compliancerule.ParameterType;
import org.iac2.entity.compliancerule.ComplianceRuleEntity;

@Data
@NoArgsConstructor
@Entity
public class ComplianceRuleParameterEntity {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String name;

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @ManyToOne
    @JoinColumn(name = "compliance_rule_id", nullable = false)
    private ComplianceRuleEntity complianceRule;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ParameterType type;

    public ComplianceRuleParameterEntity(String name, ParameterType type, ComplianceRuleEntity complianceRule) {
        this.name = name;
        this.type = type;
        this.complianceRule = complianceRule;
        this.complianceRule.getParameters().add(this);
    }
}
