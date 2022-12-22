package org.iac2.entity.productionsystem;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.iac2.entity.architecturereconstruction.ModelEnhancementStrategyEntity;
import org.iac2.entity.compliancejob.ComplianceJobEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

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
    private List<ProductionSystemProperty> properties;

    @OneToMany(mappedBy = "productionSystem")
    private List<ComplianceJobEntity> complianceJobs;

    @NotNull
    private String modelCreationPluginId;

    /***
     * Specifies an optional model enhancement strategy that is applied at the production system level after applying
     */
    @ManyToOne
    @JoinColumn(name = "model_enhancement_strategy_id")
    private ModelEnhancementStrategyEntity modelEnhancementStrategy;

    public ProductionSystemEntity(String description, String iacTechnologyName, String modelCreationPluginId) {
        this.description = description;
        this.iacTechnologyName = iacTechnologyName;
        this.isDeleted = false;
        this.modelCreationPluginId = modelCreationPluginId;
        this.properties = new ArrayList<>();
        this.complianceJobs = new ArrayList<>();
    }
}
