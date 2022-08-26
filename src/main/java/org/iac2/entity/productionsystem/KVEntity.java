package org.iac2.entity.productionsystem;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

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
