package org.iac2.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.iac2.entity.compliancejob.issue.ComplianceIssueEntity;
import org.iac2.entity.productionsystem.ProductionSystemEntity;

@Data
@NoArgsConstructor
@Entity
public class KVEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String key;

    private String value;

    @ManyToOne
    @JoinColumn(name = "production_system_id")
    private ProductionSystemEntity productionSystem;

    @ManyToOne
    @JoinColumn(name = "compliance_issue_id")
    private ComplianceIssueEntity complianceIssue;

    public KVEntity(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
