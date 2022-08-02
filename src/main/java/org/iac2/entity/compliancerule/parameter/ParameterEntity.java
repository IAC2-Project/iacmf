package org.iac2.entity.compliancerule.parameter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class ParameterEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @NotNull
    private String name;

    @Enumerated(EnumType.ORDINAL)
    private ParameterType type;

    public ParameterEntity(String name, ParameterType type) {
        this.name = name;
        this.type = type;
    }
}
