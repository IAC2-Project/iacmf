package org.iac2.common.model.compliancerule;

// careful when changing enum. see: https://www.baeldung.com/jpa-persisting-enums-in-jpa#ordinal
public enum ParameterType {
    INT,
    DECIMAL,
    STRING,
    STRING_LIST,
    BOOLEAN
}
