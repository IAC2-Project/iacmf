package org.iac2.entity.compliancejob;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.iac2.entity.compliancerule.ComplianceRuleEntity;
import org.iac2.entity.compliancerule.parameter.ComplianceRuleParameterAssignmentEntity;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class ComplianceRuleConfigurationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "complianceRuleConfiguration")
    private List<ComplianceRuleParameterAssignmentEntity> complianceRuleParameterAssignments;

    // unidirectional
    @ManyToOne
    @JoinColumn(name = "compliance_rule_id")
    private ComplianceRuleEntity complianceRule;

    @ManyToOne
    @JoinColumn(name = "compliance_job_id")
    private ComplianceJobEntity complianceJob;

    @NotNull
    private String issueType;

    public ComplianceRuleConfigurationEntity(ComplianceRuleEntity complianceRule, String issueType) {
        this.issueType = issueType;
        this.complianceRuleParameterAssignments = new ArrayList<>();
        this.complianceRule = complianceRule;
    }

    public ComplianceRuleConfigurationEntity addParameterAssignment(ComplianceRuleParameterAssignmentEntity entity) {
        entity.setComplianceRuleConfiguration(this);
        this.getComplianceRuleParameterAssignments().add(entity);

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComplianceRuleConfigurationEntity that = (ComplianceRuleConfigurationEntity) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
