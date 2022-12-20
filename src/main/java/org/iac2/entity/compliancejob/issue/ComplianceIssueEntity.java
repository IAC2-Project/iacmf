package org.iac2.entity.compliancejob.issue;

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
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.iac2.entity.KVEntity;
import org.iac2.entity.compliancejob.execution.ExecutionEntity;

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
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<IssueFixingReportEntity> fixingReports;

    @OneToMany(mappedBy = "complianceIssue")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<KVEntity> properties;

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
