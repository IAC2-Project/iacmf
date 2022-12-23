package org.iac2.entity.compliancejob;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.iac2.entity.compliancerule.parameter.ComplianceRuleParameterAssignmentEntity;

@Entity
@Data
@NoArgsConstructor
public class ComplianceRuleConfigurationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "complianceRuleConfiguration")
    private List<ComplianceRuleParameterAssignmentEntity> complianceRuleParameterAssignments;

    @ManyToOne
    @JoinColumn(name = "compliance_job_id")
    private ComplianceJobEntity complianceJob;

    @NotNull
    private String issueType;

    public ComplianceRuleConfigurationEntity(ComplianceJobEntity complianceJob, String issueType) {
        this.complianceJob = complianceJob;
        this.issueType = issueType;
        this.complianceRuleParameterAssignments = new ArrayList<>();
    }
}
