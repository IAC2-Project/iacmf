package org.iac2.entity.productionsystem;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.iac2.entity.KVEntity;
import org.iac2.entity.compliancejob.ComplianceJobEntity;

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

    @OneToMany(mappedBy = "productionSystem")
    private List<ComplianceJobEntity> complianceJobs;

    @NotNull
    private String modelCreationPluginId;


    public ProductionSystemEntity(String description, String iacTechnologyName, String modelCreationPluginId) {
        this.description = description;
        this.iacTechnologyName = iacTechnologyName;
        this.isDeleted = false;
        this.modelCreationPluginId = modelCreationPluginId;
        this.properties = new ArrayList<>();
        this.complianceJobs = new ArrayList<>();
    }
}
