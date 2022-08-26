package org.iac2.entity.compliancejob.execution;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.compliancerule.ComplianceIssueEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class ExecutionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date startTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;

    @Enumerated(EnumType.ORDINAL)
    @NotNull
    private ExecutionStep currentStep;

    @Enumerated(EnumType.ORDINAL)
    @NotNull
    private ExecutionStatus status;

    private Boolean violationsDetected;

    @ManyToOne
    @JoinColumn(name = "compliance_job_id", nullable = false)
    private ComplianceJobEntity complianceJob;

    @OneToMany(mappedBy = "execution", orphanRemoval = true)
    private List<ComplianceIssueEntity> complianceIssueEntities = new java.util.ArrayList<>();


    public ExecutionEntity(ComplianceJobEntity complianceJob) {
        this.complianceJob = complianceJob;
        this.startTime = new Date();
        this.status = ExecutionStatus.CREATED;
        this.currentStep = ExecutionStep.START;
    }

}
