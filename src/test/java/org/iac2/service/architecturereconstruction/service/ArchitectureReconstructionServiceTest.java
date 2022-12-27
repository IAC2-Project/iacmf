package org.iac2.service.architecturereconstruction.service;

import java.util.List;

import javax.transaction.Transactional;

import org.iac2.common.model.InstanceModel;
import org.iac2.entity.KVEntity;
import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.compliancejob.execution.ExecutionEntity;
import org.iac2.entity.plugin.PluginUsageEntity;
import org.iac2.entity.productionsystem.ProductionSystemEntity;
import org.iac2.repository.compliancejob.ComplianceJobRepository;
import org.iac2.repository.compliancejob.ExecutionRepository;
import org.iac2.repository.plugin.PluginUsageRepository;
import org.iac2.repository.productionsystem.KVRepository;
import org.iac2.repository.productionsystem.ProductionSystemRepository;
import org.iac2.service.architecturereconstruction.plugin.factory.ArchitectureReconstructionPluginFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@Rollback
class ArchitectureReconstructionServiceTest {
    @MockBean
    ArchitectureReconstructionPluginFactory pluginManager;

    @Autowired
    ArchitectureReconstructionService service;
    @Autowired
    private ExecutionRepository executionRepository;
    @Autowired
    private ComplianceJobRepository complianceJobRepository;
    @Autowired
    private PluginUsageRepository pluginUsageRepository;
    @Autowired
    private KVRepository kVRepository;
    @Autowired
    private ProductionSystemRepository productionSystemRepository;

    @Test
    void testARService() {
        Mockito.when(pluginManager.createPlugin(anyString()))
                .thenReturn(new MockModelCreationPlugin(3));
        PluginUsageEntity usageEntity = new PluginUsageEntity("testPlugin");
        PluginUsageEntity checking = new PluginUsageEntity("checkingPlugin");
        pluginUsageRepository.save(checking);
        pluginUsageRepository.save(usageEntity);
        ProductionSystemEntity system = new ProductionSystemEntity(
                "this is a production system",
                "opentoscacontainer",
                usageEntity
        );
        productionSystemRepository.save(system);
        List<KVEntity> properties = List.of(new KVEntity("nodes", "3"));
        kVRepository.saveAll(properties);
        ComplianceJobEntity complianceJob = new ComplianceJobEntity("job1", system, checking);
        complianceJobRepository.save(complianceJob);
        ExecutionEntity execution = new ExecutionEntity(complianceJob);
        executionRepository.save(execution);
        InstanceModel model = service.crteateInstanceModel(system, execution);
        Assertions.assertEquals(4, model.getDeploymentModel().getComponents().size());
    }
}
