package org.iac2.entity.compliancejob.issue;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.iac2.entity.KVEntity;
import org.iac2.entity.compliancejob.execution.ExecutionEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class ComplianceIssueEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "execution_id", nullable = false)
    private ExecutionEntity execution;

    @OneToMany(mappedBy = "complianceIssue")
    private List<IssueFixingReportEntity> fixingReports;

    @OneToMany(mappedBy = "complianceIssue")
    private List<KVEntity> properties;

    @Null
    private String description;

    @NotNull
    private String type;

    public ComplianceIssueEntity(ExecutionEntity execution, String description, String type) {
        this.execution = execution;
        this.description = description;
        this.type = type;
        fixingReports = new ArrayList<>();
        properties = new ArrayList<>();
    }
}
