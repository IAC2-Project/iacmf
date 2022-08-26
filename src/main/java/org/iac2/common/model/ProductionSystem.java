package org.iac2.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;


@Data
@AllArgsConstructor
public class ProductionSystem {
    private String iacTechnologyName;
    private String description;
    private Map<String, String> properties;
}
