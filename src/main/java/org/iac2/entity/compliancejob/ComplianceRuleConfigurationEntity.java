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

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @OneToMany(mappedBy = "complianceRuleConfiguration")
    private List<ComplianceRuleParameterAssignmentEntity> complianceRuleParameterAssignments;

    // unidirectional
    @ManyToOne
    @JoinColumn(name = "compliance_rule_id")
    private ComplianceRuleEntity complianceRule;

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @ManyToOne
    @JoinColumn(name = "compliance_job_id")
    private ComplianceJobEntity complianceJob;

    @NotNull
    private String issueType;

    public ComplianceRuleConfigurationEntity(ComplianceRuleEntity complianceRule, ComplianceJobEntity complianceJob, String issueType) {
        this.issueType = issueType;
        this.complianceRuleParameterAssignments = new ArrayList<>();
        this.complianceRule = complianceRule;
        this.complianceJob = complianceJob;
        this.complianceJob.getComplianceRuleConfigurations().add(this);
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
