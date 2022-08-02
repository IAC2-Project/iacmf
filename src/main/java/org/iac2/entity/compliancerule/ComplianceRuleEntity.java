package org.iac2.entity.compliancerule;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.compliancerule.parameter.ParameterEntity;

@Entity
@Data
@NoArgsConstructor
public class ComplianceRuleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String type;

    @NotNull
    private String location;

    private String description;

    @NotNull
    private Boolean isDeleted;


    @OneToMany(mappedBy = "complianceRule")
    private List<ParameterEntity> parameters;


    @OneToMany(mappedBy = "complianceRule")
    private List<ComplianceJobEntity> jobs;

    public ComplianceRuleEntity(String type, String location, String description) {
        this.type = type;
        this.location = location;
        this.description = description;
        this.isDeleted = false;
    }
}