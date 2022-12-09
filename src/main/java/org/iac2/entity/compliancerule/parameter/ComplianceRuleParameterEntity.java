package org.iac2.entity.compliancerule.parameter;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.iac2.entity.compliancerule.ComplianceRuleEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

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

    @Enumerated(EnumType.ORDINAL)
    @NotNull
    private ParameterType type;

    public ComplianceRuleParameterEntity(String name, ParameterType type) {
        this.name = name;
        this.type = type;
    }
}
