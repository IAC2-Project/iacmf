package org.iac2.entity.plugin;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.compliancejob.issue.IssueFixingConfigurationEntity;
import org.iac2.entity.productionsystem.ProductionSystemEntity;

@Entity
@Data
@NoArgsConstructor
public class PluginUsageEntity extends PluginUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // refinement plugin
    @ManyToOne
    @JoinColumn(name = "compliance_job_id_refinement")
    private ComplianceJobEntity complianceJobRefinement;

    // refinement plugin
    private Integer refinementPluginIndexInComplianceJob;

    // checking plugin
    @OneToOne(mappedBy = "checkingPluginUsage")
    private ComplianceJobEntity complianceJobChecking;

    // checking plugin
    @OneToOne(mappedBy = "pluginUsage")
    private IssueFixingConfigurationEntity issueFixingConfiguration;

    // model creation plugin
    @OneToOne(mappedBy = "modelCreationPluginUsage")
    private ProductionSystemEntity productionSystem;

    private PluginUsageEntity(String pluginIdentifier,
                              ComplianceJobEntity complianceJobRefinement,
                              Integer refinementPluginIndexInComplianceJob,
                              ComplianceJobEntity complianceJobChecking,
                              IssueFixingConfigurationEntity issueFixingConfiguration,
                              ProductionSystemEntity productionSystem,
                              List<PluginConfigurationEntity> pluginConfiguration) {
        super(pluginIdentifier, pluginConfiguration);
        this.complianceJobRefinement = complianceJobRefinement;
        this.refinementPluginIndexInComplianceJob = refinementPluginIndexInComplianceJob;
        this.complianceJobChecking = complianceJobChecking;
        this.issueFixingConfiguration = issueFixingConfiguration;
        this.productionSystem = productionSystem;
    }

    public PluginUsageEntity(String pluginIdentifier,
                             ComplianceJobEntity complianceJobRefinement,
                             Integer refinementPluginIndexInComplianceJob,
                             List<PluginConfigurationEntity> pluginConfiguration) {
        this(pluginIdentifier, complianceJobRefinement, refinementPluginIndexInComplianceJob, null, null, null, pluginConfiguration);
    }

    public PluginUsageEntity(String pluginIdentifier, ComplianceJobEntity complianceJobChecking, List<PluginConfigurationEntity> pluginConfiguration) {
        this(pluginIdentifier, null, null, complianceJobChecking, null, null, pluginConfiguration);
    }

    public PluginUsageEntity(String pluginIdentifier, IssueFixingConfigurationEntity issueFixingConfiguration, List<PluginConfigurationEntity> pluginConfiguration) {
        this(pluginIdentifier, null, null, null, issueFixingConfiguration, null, pluginConfiguration);
    }

    public PluginUsageEntity(String pluginIdentifier, ProductionSystemEntity productionSystem, List<PluginConfigurationEntity> pluginConfiguration) {
        this(pluginIdentifier, null, null, null, null, productionSystem, pluginConfiguration);
    }
}
