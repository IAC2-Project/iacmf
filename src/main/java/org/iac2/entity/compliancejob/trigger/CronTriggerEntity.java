package org.iac2.entity.compliancejob.trigger;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.scheduling.support.CronExpression;

@Entity
@DiscriminatorValue(value = "1")
@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
public class CronTriggerEntity extends TriggerEntity{
    private String cronExpression;

    public CronTriggerEntity(String description, String cronExpression) {
        super(description);
        if (!CronExpression.isValidExpression(cronExpression)) {
            throw new IllegalArgumentException("invalid cron expression!");
        }

        this.cronExpression = cronExpression;
    }
}
