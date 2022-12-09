package org.iac2.entity.compliancejob.trigger;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.springframework.scheduling.support.CronExpression;

@Entity
@Data
@NoArgsConstructor
public class TriggerEntity {
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

    @Getter
    private String cronExpression;

    @ManyToMany(mappedBy = "triggers")
    private List<ComplianceJobEntity> complianceJobs;

    public TriggerEntity(String description) {
        this.description = description;
        this.isDeleted = false;
        this.complianceJobs = new ArrayList<>();
        this.cronExpression = "";
    }

    public void setCronExpression(String cronExpression) {
        if (cronExpression != null && !cronExpression.isEmpty() && !CronExpression.isValidExpression(cronExpression)) {
            throw new IllegalArgumentException("invalid cron expression!");
        }

        this.cronExpression = cronExpression;
    }
}
