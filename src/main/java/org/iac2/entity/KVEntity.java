package org.iac2.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iac2.entity.compliancejob.issue.ComplianceIssueEntity;
import org.iac2.entity.productionsystem.ProductionSystemEntity;

@Data
@NoArgsConstructor
@Entity
public class KVEntity {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String key;

    private String value;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "production_system_id")
    private ProductionSystemEntity productionSystem;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "compliance_issue_id")
    private ComplianceIssueEntity complianceIssue;

    public KVEntity(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
