package org.iac2.entity.compliancejob.execution;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.iac2.common.model.compliancejob.execution.ExecutionStatus;
import org.iac2.common.model.compliancejob.execution.ExecutionStep;
import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.compliancejob.issue.ComplianceIssueEntity;

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

    @Enumerated(EnumType.STRING)
    @NotNull
    private ExecutionStep currentStep;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ExecutionStatus status;

    @Column(length = 10000)
    private String instanceModel;

    private Boolean violationsDetected;

    private String description;

    @ManyToOne
    @JoinColumn(name = "compliance_job_id", nullable = false)
    private ComplianceJobEntity complianceJob;

    @OneToMany(mappedBy = "execution", fetch = FetchType.EAGER)
    private List<ComplianceIssueEntity> complianceIssueEntities;


    public ExecutionEntity(ComplianceJobEntity complianceJob) {
        this.complianceJob = complianceJob;
        this.startTime = new Date();
        this.status = ExecutionStatus.CREATED;
        this.currentStep = ExecutionStep.START;
        this.instanceModel = "";
        this.violationsDetected = false;
        this.complianceIssueEntities = new ArrayList<>();
    }

}
