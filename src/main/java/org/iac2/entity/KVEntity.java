package org.iac2.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.iac2.entity.compliancejob.issue.ComplianceIssueEntity;
import org.iac2.entity.plugin.PluginConfigurationEntity;
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

    @Setter(AccessLevel.NONE)
    @ManyToOne
    @JoinColumn(name = "plugin_configuration_id", nullable = true)
    private PluginConfigurationEntity pluginConfiguration;

    @Setter(AccessLevel.NONE)
    @ManyToOne
    @JoinColumn(name = "production_system_id", nullable = true)
    private ProductionSystemEntity productionSystem;

    @Setter(AccessLevel.NONE)
    @ManyToOne
    @JoinColumn(name = "compliance_issue_id", nullable = true)
    private ComplianceIssueEntity complianceIssue;

    private KVEntity(String key, String value, PluginConfigurationEntity pluginConfiguration,
                     ProductionSystemEntity productionSystemEntity, ComplianceIssueEntity complianceIssueEntity) {
        this.key = key;
        this.value = value;
        this.pluginConfiguration = pluginConfiguration;
        this.productionSystem = productionSystemEntity;
        this.complianceIssue = complianceIssueEntity;
    }

    public KVEntity(String key, String value, PluginConfigurationEntity pluginConfiguration) {
        this(key, value, pluginConfiguration, null, null);
    }

    public KVEntity(String key, String value, ProductionSystemEntity productionSystemEntity) {
        this(key, value, null, productionSystemEntity, null);
    }

    public KVEntity(String key, String value, ComplianceIssueEntity complianceIssueEntity) {
        this(key, value, null, null, complianceIssueEntity);
    }
}
