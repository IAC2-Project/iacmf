package org.iac2.entity.productionsystem;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.iac2.entity.KVEntity;
import org.iac2.entity.plugin.PluginUsageEntity;

@Entity
@NoArgsConstructor
@Data
public class ProductionSystemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private Boolean isDeleted;

    @NotNull
    private String iacTechnologyName;

    private String description;

    @OneToMany(mappedBy = "productionSystem")
    private List<KVEntity> properties;

    @OneToOne
    @JoinColumn(name = "model_creation_plugin_usage_id", nullable = false)
    private PluginUsageEntity modelCreationPluginUsage;

    public ProductionSystemEntity(String description, String iacTechnologyName) {
        this.description = description;
        this.iacTechnologyName = iacTechnologyName;
        this.isDeleted = false;
        this.properties = new ArrayList<>();
    }

    public void setModelCreationPluginUsage(PluginUsageEntity entity) {
        entity.setProductionSystem(this);
        this.modelCreationPluginUsage = entity;
    }
}
