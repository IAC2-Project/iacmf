package org.iac2.entity.compliancerule;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iac2.entity.compliancerule.parameter.ComplianceRuleParameterEntity;

@Entity
@Data
@NoArgsConstructor
public class ComplianceRuleEntity {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String type;

    @NotNull
    private String location;

    private String description;

    @NotNull
    private Boolean isDeleted;

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @OneToMany(mappedBy = "complianceRule")
    private List<ComplianceRuleParameterEntity> parameters;

    public ComplianceRuleEntity(String name, String type, String location, String description) {
        this.name = name;
        this.type = type;
        this.location = location;
        this.description = description;
        this.isDeleted = false;
        this.parameters = new ArrayList<>();
    }
}
