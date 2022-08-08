package org.iac2.architecturereconstruction.service;

import java.util.List;

import org.iac2.architecturereconstruction.plugin.manager.ArchitectureReconstructionPluginManager;
import org.iac2.common.model.SystemModel;
import org.iac2.entity.architecturereconstruction.ModelEnhancementStrategyEntity;
import org.iac2.entity.productionsystem.KVEntity;
import org.iac2.entity.productionsystem.ProductionSystemEntity;
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
        system.setKvPairs(List.of(new KVEntity("nodes", "3", system)));
        system.setModelEnhancementStrategy(new ModelEnhancementStrategyEntity(List.of()));


        String ID = "ID";
        SystemModel model = service.reconstructArchitectureForProductionSystem(system);
        Assertions.assertEquals(3, model.getDeploymentModel().getComponents().size());


    }

}