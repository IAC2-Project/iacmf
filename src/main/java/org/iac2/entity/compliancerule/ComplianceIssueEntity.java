package org.iac2.entity.compliancerule;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.iac2.entity.compliancejob.execution.ExecutionEntity;

import javax.persistence.*;

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

    private String description;
}
