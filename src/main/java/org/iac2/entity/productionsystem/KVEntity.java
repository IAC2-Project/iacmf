package org.iac2.entity.productionsystem;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class KVEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String key;

    private String value;

    @ManyToOne
    @JoinColumn(name = "production_system_id", nullable = false)
    private ProductionSystemEntity productionSystem;

    public KVEntity(String key, String value, ProductionSystemEntity productionSystem) {
        this.key = key;
        this.value = value;
        this.productionSystem = productionSystem;
    }
}
