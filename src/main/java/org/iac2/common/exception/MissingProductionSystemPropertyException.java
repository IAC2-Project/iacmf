package org.iac2.common.exception;

import lombok.Getter;
import org.iac2.common.model.ProductionSystem;

@Getter
public class MissingProductionSystemPropertyException extends IacmfException {
    private final ProductionSystem productionSystem;
    private final String propertyName;

    public MissingProductionSystemPropertyException(ProductionSystem productionSystem, String propertyName) {
        super("The production system: '%s' is missing a required property: %s".formatted(productionSystem.getDescription(), propertyName));
        this.productionSystem = productionSystem;
        this.propertyName = propertyName;
    }
}
