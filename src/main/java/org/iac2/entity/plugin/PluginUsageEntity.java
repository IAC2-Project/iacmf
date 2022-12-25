package org.iac2.entity.plugin;

import java.util.ArrayList;

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

    public PluginUsageEntity(String pluginIdentifier) {
        super(pluginIdentifier, new ArrayList<>());
    }

    public PluginUsageEntity addPluginConfiguration(PluginConfigurationEntity entity) {
        entity.setPluginUsage(this);
        this.getPluginConfiguration().add(entity);

        return this;
    }
}
