package org.iac2.entity.compliancejob.trigger;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.iac2.entity.compliancejob.ComplianceJobEntity;

@Entity
@Data
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.INTEGER)
public abstract class TriggerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String description;

    /**
     * if set to true, this indicates that this trigger must not be shown to the UI.
     * However, triggers are not removed from the database, since they might be referenced by job executions.
     */
    @NotNull
    private Boolean isDeleted;

    @ManyToMany(mappedBy = "triggers")
    private List<ComplianceJobEntity> complianceJobs;

    public TriggerEntity(String description) {
        this.description = description;
        this.isDeleted = false;
        this.complianceJobs = new ArrayList<>();
    }

}
