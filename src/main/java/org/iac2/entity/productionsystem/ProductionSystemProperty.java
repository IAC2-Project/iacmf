package org.iac2.entity.productionsystem;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.iac2.entity.KVEntity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue(value = "1")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ProductionSystemProperty extends KVEntity {
    @ManyToOne
    @JoinColumn(name = "production_system_id")
    private ProductionSystemEntity productionSystem;

    public ProductionSystemProperty(String key, String value, ProductionSystemEntity productionSystem) {
        super(key, value);
        this.productionSystem = productionSystem;
    }

}
