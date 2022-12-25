package org.iac2.entity.productionsystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.iac2.entity.KVEntity;
import org.iac2.entity.plugin.PluginUsageEntity;

@Entity
@NoArgsConstructor
@Setter
@Getter
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

    public ProductionSystemEntity addProperty(KVEntity property) {
        this.getProperties().add(property);
        property.setProductionSystem(this);

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductionSystemEntity that = (ProductionSystemEntity) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
