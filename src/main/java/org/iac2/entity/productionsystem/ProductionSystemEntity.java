package org.iac2.entity.productionsystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private Boolean isDeleted;

    @NotNull
    private String iacTechnologyName;

    private String description;

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @OneToMany(mappedBy = "productionSystem", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<KVEntity> properties;

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @OneToOne
    @JoinColumn(name = "model_creation_plugin_usage_id", nullable = false)
    private PluginUsageEntity modelCreationPluginUsage;

    public ProductionSystemEntity(String name, String description, String iacTechnologyName, PluginUsageEntity modelCreationPluginUsage) {
        this.name = name;
        this.description = description;
        this.iacTechnologyName = iacTechnologyName;
        this.isDeleted = false;
        this.properties = new ArrayList<>();
        this.modelCreationPluginUsage = modelCreationPluginUsage;
        this.modelCreationPluginUsage.setProductionSystem(this);
    }

    public ProductionSystemEntity addProperty(KVEntity property) {
        property.setProductionSystem(this);
        this.getProperties().add(property);

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
