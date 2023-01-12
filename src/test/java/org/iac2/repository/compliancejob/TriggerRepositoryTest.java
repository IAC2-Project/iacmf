package org.iac2.repository.compliancejob;

import java.util.Arrays;
import java.util.List;

import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.compliancejob.trigger.TriggerEntity;
import org.iac2.entity.compliancerule.ComplianceRuleEntity;
import org.iac2.entity.plugin.PluginUsageEntity;
import org.iac2.entity.productionsystem.ProductionSystemEntity;
import org.iac2.repository.compliancerule.ComplianceRuleRepository;
import org.iac2.repository.plugin.PluginUsageRepository;
import org.iac2.repository.productionsystem.ProductionSystemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TriggerRepositoryTest {
    @Autowired
    private ComplianceRuleRepository complianceRuleRepository;
    @Autowired
    private ProductionSystemRepository productionSystemRepository;
    @Autowired
    private TriggerRepository triggerRepository;
    @Autowired
    private ComplianceJobRepository complianceJobRepository;
    @Autowired
    private PluginUsageRepository pluginUsageRepository;

    @Test
    void testTriggersOfJobQuery() {
        ComplianceRuleEntity complianceRule1 = new ComplianceRuleEntity(
                "cr1",
                "polymorphism",
                "https://localhost/rule-1",
                "my awesome rule"
        );

        ComplianceRuleEntity complianceRule2 = new ComplianceRuleEntity(
                "cr2",
                "polymorphism",
                "https://localhost/rule-2",
                "my very awesome rule"
        );

        this.complianceRuleRepository.save(complianceRule1);
        this.complianceRuleRepository.save(complianceRule2);

        PluginUsageEntity usage = new PluginUsageEntity("opentoscaplugin");
        this.pluginUsageRepository.save(usage);
        PluginUsageEntity checkingPluginUsage = new PluginUsageEntity("checkingPlugin");
        pluginUsageRepository.save(checkingPluginUsage);
        ProductionSystemEntity productionSystem = new ProductionSystemEntity("abc", "this is the best production system",
                "opentoscacontainer", usage);
        this.productionSystemRepository.save(productionSystem);

        TriggerEntity trigger1 = new TriggerEntity("t1", "Fire at 12:00 PM (noon) every day");
        trigger1.setCronExpression("0 0 12 * * ?");
        TriggerEntity trigger2 = new TriggerEntity("t2", "Fire at 10:15 AM every day");
        trigger2.setCronExpression("0 15 10 * * ?");
        TriggerEntity trigger3 = new TriggerEntity("t3", "Fire every November 11 at 11:11 AM");
        trigger3.setCronExpression("0 11 11 11 11 ?");

        this.triggerRepository.save(trigger1);
        this.triggerRepository.save(trigger2);
        this.triggerRepository.save(trigger3);

        List<TriggerEntity> triggers1 = Arrays.asList(trigger1, trigger3);
        List<TriggerEntity> triggers2 = List.of(trigger2);

        ComplianceJobEntity job1 = new ComplianceJobEntity(
                "job1",
                "this is job 1",
                productionSystem,
                checkingPluginUsage);
        job1.addTrigger(trigger1).addTrigger(trigger3);
        ComplianceJobEntity job2 = new ComplianceJobEntity(
                "job2",
                "this is job 2",
                productionSystem,
                checkingPluginUsage);
        job2.addTrigger(trigger2);

        this.complianceJobRepository.save(job1);
        this.complianceJobRepository.save(job2);

        List<TriggerEntity> triggers = this.triggerRepository.findAllTriggersOfJob(job1.getId());
        assertEquals(triggers1.size(), triggers.size());

        triggers = this.triggerRepository.findAllTriggersOfJob(job2.getId());
        assertEquals(triggers2.size(), triggers.size());
    }
}
