package org.iac2.entity.compliancerule.parameter.assignment;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.compliancerule.parameter.ParameterEntity;

@Data
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.INTEGER)
public abstract class ParameterAssignmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    protected ParameterEntity parameter;

    @ManyToOne
    @JoinColumn(nullable = false)
    protected ComplianceJobEntity complianceJob;

    public ParameterAssignmentEntity(ParameterEntity parameter, ComplianceJobEntity complianceJob) {
        this.parameter = parameter;
        this.complianceJob = complianceJob;
    }
}
