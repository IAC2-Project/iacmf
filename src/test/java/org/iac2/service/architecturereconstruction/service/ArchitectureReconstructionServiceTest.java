package org.iac2.service.architecturereconstruction.service;

import java.util.List;

import org.iac2.common.model.InstanceModel;
import org.iac2.entity.KVEntity;
import org.iac2.entity.architecturereconstruction.ModelEnhancementStrategyEntity;
import org.iac2.entity.productionsystem.ProductionSystemEntity;
import org.iac2.service.architecturereconstruction.plugin.manager.ArchitectureReconstructionPluginManager;
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
    ArchitectureReconstructionPluginManager pluginManager;

    @Autowired
    ArchitectureReconstructionService service;

    @Test
    void testARService() {
        Mockito.when(pluginManager.getModelCreationPlugin(anyString()))
                .thenReturn(new MockModelCreationPlugin(3));

        ProductionSystemEntity system = new ProductionSystemEntity(
                "this is a production system",
                "opentoscacontainer",
                "testPlugin");
        system.setProperties(List.of(new KVEntity("nodes", "3", system)));
        system.setModelEnhancementStrategy(new ModelEnhancementStrategyEntity(List.of()));


        String ID = "ID";
        InstanceModel model = service.reconstructArchitectureForProductionSystem(system);
        Assertions.assertEquals(4, model.getDeploymentModel().getComponents().size());


    }

}
