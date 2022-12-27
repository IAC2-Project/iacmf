package org.iac2.entity.compliancejob.issue;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.plugin.PluginUsageEntity;

@Entity
@Data
@NoArgsConstructor
public class IssueFixingConfigurationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String issueType;

    @OneToOne
    @JoinColumn(name = "plugin_usage_id", nullable = false)
    private PluginUsageEntity pluginUsage;

    @ManyToOne
    @JoinColumn(name = "compliance_job_id", nullable = false)
    private ComplianceJobEntity complianceJob;

    public IssueFixingConfigurationEntity(String issueType, ComplianceJobEntity complianceJob, PluginUsageEntity pluginUsage) {
        this.issueType = issueType;
        this.complianceJob = complianceJob;
        this.complianceJob.getIssueFixingConfigurations().add(this);
        this.pluginUsage = pluginUsage;
        this.pluginUsage.setIssueFixingConfiguration(this);
    }
}