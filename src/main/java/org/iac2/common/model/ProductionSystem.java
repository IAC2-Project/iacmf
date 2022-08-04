package org.iac2.common.model;

import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class ProductionSystem {
    private String iacTechnologyName;
    private String description;
    private List<Map.Entry<String,String>> properties;
}
