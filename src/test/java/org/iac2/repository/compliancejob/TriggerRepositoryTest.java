package org.iac2.repository.compliancejob;

import java.util.Arrays;
import java.util.List;

import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.compliancejob.trigger.TriggerEntity;
import org.iac2.entity.compliancerule.ComplianceRuleEntity;
import org.iac2.entity.plugin.architecturereconstruction.ModelEnhancementStrategyEntity;
import org.iac2.entity.productionsystem.ProductionSystemEntity;
import org.iac2.repository.compliancerule.ComplianceRuleRepository;
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

        TriggerEntity trigger1 = new TriggerEntity("Fire at 12:00 PM (noon) every day");
        trigger1.setCronExpression("0 0 12 * * ?");
        TriggerEntity trigger2 = new TriggerEntity("Fire at 10:15 AM every day");
        trigger2.setCronExpression("0 15 10 * * ?");
        TriggerEntity trigger3 = new TriggerEntity("Fire every November 11 at 11:11 AM");
        trigger3.setCronExpression("0 11 11 11 11 ?");

        this.triggerRepository.save(trigger1);
        this.triggerRepository.save(trigger2);
        this.triggerRepository.save(trigger3);

        List<TriggerEntity> triggers1 = Arrays.asList(trigger1, trigger3);
        List<TriggerEntity> triggers2 = Arrays.asList(trigger2);

        ModelEnhancementStrategyEntity strategy1 = new ModelEnhancementStrategyEntity(List.of("p1", "p2"));
        ModelEnhancementStrategyEntity strategy2 = new ModelEnhancementStrategyEntity(List.of("p3", "p4"));
        modelEnhancementStrategyRepository.save(strategy1);
        modelEnhancementStrategyRepository.save(strategy2);

        ComplianceJobEntity job1 = new ComplianceJobEntity(
                "this is job 1",
                "mcp1",
                "mfp1",
                productionSystem,
                complianceRule1,
                strategy1,
                triggers1);
        ComplianceJobEntity job2 = new ComplianceJobEntity(
                "this is job 2",
                "mcp2",
                "mfp2",
                productionSystem,
                complianceRule2,
                strategy2,
                triggers2);

        this.complianceJobRepository.save(job1);
        this.complianceJobRepository.save(job2);

        List<TriggerEntity> triggers = this.triggerRepository.findAllTriggersOfJob(job1.getId());
        assertEquals(triggers1.size(), triggers.size());

        triggers = this.triggerRepository.findAllTriggersOfJob(job2.getId());
        assertEquals(triggers2.size(), triggers.size());
    }
}
