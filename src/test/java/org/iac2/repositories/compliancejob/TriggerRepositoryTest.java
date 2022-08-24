package org.iac2.repositories.compliancejob;

import java.util.Arrays;
import java.util.List;

import org.iac2.entity.architecturereconstruction.ModelEnhancementStrategyEntity;
import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.compliancejob.trigger.CronTriggerEntity;
import org.iac2.entity.compliancejob.trigger.TriggerEntity;
import org.iac2.entity.compliancerule.ComplianceRuleEntity;
import org.iac2.entity.productionsystem.ProductionSystemEntity;
import org.iac2.repositories.compliancerule.ComplianceRuleRepository;
import org.iac2.repositories.productionsystem.ProductionSystemRepository;
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
    private ModelEnhancementStrategyRepository modelEnhancementStrategyRepository;

    @Test
    void testTriggersOfJobQuery() {
        ComplianceRuleEntity complianceRule1 = new ComplianceRuleEntity(
                "polymorphism",
                "https://localhost/rule-1",
                "my awesome rule"
                );

        ComplianceRuleEntity complianceRule2 = new ComplianceRuleEntity(
                "polymorphism",
                "https://localhost/rule-2",
                "my very awesome rule"
        );

        this.complianceRuleRepository.save(complianceRule1);
        this.complianceRuleRepository.save(complianceRule2);

        ProductionSystemEntity productionSystem = new ProductionSystemEntity("this is the best production system",
                "opentoscacontainer", "opentoscaplugin");
        this.productionSystemRepository.save(productionSystem);

        CronTriggerEntity trigger1 = new CronTriggerEntity("Fire at 12:00 PM (noon) every day", "0 0 12 * * ?");
        CronTriggerEntity trigger2 = new CronTriggerEntity("Fire at 10:15 AM every day", "0 15 10 * * ?");
        CronTriggerEntity trigger3 = new CronTriggerEntity("Fire every November 11 at 11:11 AM", "0 11 11 11 11 ?");

        this.triggerRepository.save(trigger1);
        this.triggerRepository.save(trigger2);
        this.triggerRepository.save(trigger3);

        List<TriggerEntity> triggers1 = Arrays.asList(new TriggerEntity[]{trigger1, trigger3});
        List<TriggerEntity> triggers2 = Arrays.asList(new TriggerEntity[]{trigger2});

        ModelEnhancementStrategyEntity strategy1 = new ModelEnhancementStrategyEntity(List.of("p1", "p2"));
        ModelEnhancementStrategyEntity strategy2 = new ModelEnhancementStrategyEntity(List.of("p3", "p4"));
        modelEnhancementStrategyRepository.save(strategy1);
        modelEnhancementStrategyRepository.save(strategy2);

        ComplianceJobEntity job1 = new ComplianceJobEntity("this is job 1", productionSystem, complianceRule1, strategy1,
                triggers1);
        ComplianceJobEntity job2 = new ComplianceJobEntity("this is job 2", productionSystem, complianceRule2, strategy2,
                triggers2);

        this.complianceJobRepository.save(job1);
        this.complianceJobRepository.save(job2);

        List<TriggerEntity> triggers = this.triggerRepository.findAllTriggersOfJob(job1.getId());
        assertEquals(triggers1.size(), triggers.size());

        triggers = this.triggerRepository.findAllTriggersOfJob(job2.getId());
        assertEquals(triggers2.size(), triggers.size());
    }
}