package org.iac2.entity.productionsystem;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;
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

    private String description;

    @OneToMany(mappedBy = "productionSystem")
    private List<KVEntity> kvPairs;

    @OneToMany(mappedBy = "productionSystem")
    private List<ComplianceJobEntity> complianceJobs;

    public ProductionSystemEntity(String description) {
        this.description = description;
        this.isDeleted = false;
    }
}
