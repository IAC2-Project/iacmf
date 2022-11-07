package org.iac2.api.model;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InstanceModelPojo {
    private String edmmModel;
    private Map<String, String> properties;
}
