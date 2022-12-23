package org.iac2.entity.plugin;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.compliancejob.issue.IssueFixingConfigurationEntity;
import org.iac2.entity.productionsystem.ProductionSystemEntity;

@Entity
@Data
@NoArgsConstructor
public class PluginUsageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String pluginIdentifier;

    @ManyToOne
    @JoinColumn(name = "compliance_job_id_refinement")
    private ComplianceJobEntity complianceJobRefinement;

    private Integer refinementPluginIndexInComplianceJob;

    @OneToOne(mappedBy = "checkingPluginUsage")
    private ComplianceJobEntity complianceJobChecking;

    @OneToOne(mappedBy = "pluginUsage")
    private IssueFixingConfigurationEntity issueFixingConfiguration;

    @OneToOne(mappedBy = "modelCreationPluginUsage")
    private ProductionSystemEntity productionSystem;

    @OneToMany(mappedBy = "pluginUsage")
    private List<PluginConfigurationEntity> pluginConfiguration;

    @OneToMany(mappedBy = "pluginUsage")
    private List<PluginUsageInstanceEntity> instances;

    private PluginUsageEntity(String pluginIdentifier,
                              ComplianceJobEntity complianceJobRefinement,
                              Integer refinementPluginIndexInComplianceJob,
                              ComplianceJobEntity complianceJobChecking,
                              IssueFixingConfigurationEntity issueFixingConfiguration,
                              ProductionSystemEntity productionSystem) {
        this.pluginIdentifier = pluginIdentifier;
        this.complianceJobRefinement = complianceJobRefinement;
        this.refinementPluginIndexInComplianceJob = refinementPluginIndexInComplianceJob;
        this.complianceJobChecking = complianceJobChecking;
        this.issueFixingConfiguration = issueFixingConfiguration;
        this.productionSystem = productionSystem;
        this.instances = new ArrayList<>();
    }

    public PluginUsageEntity(String pluginIdentifier, ComplianceJobEntity complianceJobRefinement, Integer refinementPluginIndexInComplianceJob) {
        this(pluginIdentifier, complianceJobRefinement, refinementPluginIndexInComplianceJob, null, null, null);
    }

    public PluginUsageEntity(String pluginIdentifier, ComplianceJobEntity complianceJobChecking) {
        this(pluginIdentifier, null, null, complianceJobChecking, null, null);
    }

    public PluginUsageEntity(String pluginIdentifier, IssueFixingConfigurationEntity issueFixingConfiguration) {
        this(pluginIdentifier, null, null, null, issueFixingConfiguration, null);
    }

    public PluginUsageEntity(String pluginIdentifier, ProductionSystemEntity productionSystem) {
        this(pluginIdentifier, null, null, null, null, productionSystem);
    }
}
