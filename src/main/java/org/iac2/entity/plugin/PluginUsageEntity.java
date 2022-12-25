package org.iac2.entity.plugin;

import java.util.ArrayList;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.compliancejob.issue.IssueFixingConfigurationEntity;
import org.iac2.entity.productionsystem.ProductionSystemEntity;

@Entity
@Setter
@Getter
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PluginUsageEntity that = (PluginUsageEntity) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
}
