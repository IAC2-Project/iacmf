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

import lombok.Data;
import lombok.NoArgsConstructor;
import org.iac2.entity.compliancerule.ComplianceRuleEntity;

@Data
@NoArgsConstructor
@Entity
public class ComplianceRuleParameterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String name;

    @ManyToOne
    @JoinColumn(name = "compliance_rule_id", nullable = false)
    private ComplianceRuleEntity complianceRule;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ParameterType type;

    public ComplianceRuleParameterEntity(String name, ParameterType type) {
        this.name = name;
        this.type = type;
    }
}
