package org.iac2.entity.compliancejob.issue;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
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
import org.iac2.entity.compliancejob.ComplianceRuleConfigurationEntity;
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

    // the following relation is unidirectional
    @ManyToOne
    @JoinColumn(name = "compliance_rule_configuration_id", nullable = false)
    private ComplianceRuleConfigurationEntity complianceRuleConfiguration;

    @OneToMany(mappedBy = "complianceIssue")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<IssueFixingReportEntity> fixingReports;

    @OneToMany(mappedBy = "complianceIssue", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<KVEntity> properties;

    private String description;

    @NotNull
    private String type;

    public ComplianceIssueEntity(ComplianceRuleConfigurationEntity complianceRuleConfiguration,
                                 ExecutionEntity execution,
                                 String description,
                                 String type) {
        this.complianceRuleConfiguration = complianceRuleConfiguration;
        this.description = description;
        this.type = type;
        this.execution = execution;
        this.execution.getComplianceIssueEntities().add(this);
        fixingReports = new ArrayList<>();
        properties = new ArrayList<>();
    }
    
    public ComplianceIssueEntity addProperty(KVEntity property) {
        property.setComplianceIssue(this);
        this.getProperties().add(property);

        return this;
    }
}
