package org.iac2.service.architecturereconstruction.service;

import java.util.List;

import org.iac2.common.model.InstanceModel;
import org.iac2.entity.KVEntity;
import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.compliancejob.execution.ExecutionEntity;
import org.iac2.entity.plugin.PluginUsageEntity;
import org.iac2.entity.productionsystem.ProductionSystemEntity;
import org.iac2.service.architecturereconstruction.plugin.factory.ArchitectureReconstructionPluginFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ArchitectureReconstructionServiceTest {
    @MockBean
    ArchitectureReconstructionPluginFactory pluginManager;

    @Autowired
    ArchitectureReconstructionService service;

    @Test
    void testARService() {
        Mockito.when(pluginManager.createModelCreationPlugin(anyString()))
                .thenReturn(new MockModelCreationPlugin(3));
        PluginUsageEntity usageEntity = new PluginUsageEntity("testPlugin");
        ProductionSystemEntity system = new ProductionSystemEntity(
                "this is a production system",
                "opentoscacontainer"
        );
        system.setModelCreationPluginUsage(usageEntity);
        system.setProperties(List.of(new KVEntity("nodes", "3", system)));
        ComplianceJobEntity complianceJob = new ComplianceJobEntity();
        ExecutionEntity execution = new ExecutionEntity(complianceJob);
        InstanceModel model = service.crteateInstanceModel(system, execution);
        Assertions.assertEquals(4, model.getDeploymentModel().getComponents().size());
    }
}
