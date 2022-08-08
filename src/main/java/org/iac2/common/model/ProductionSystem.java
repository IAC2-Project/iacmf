package org.iac2.common.model;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ProductionSystem {
    private String iacTechnologyName;
    private String description;
    private Map<String,String> properties;
}
