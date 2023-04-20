package org.iac2.service.architecturereconstruction.plugin.implementation.kubernetes;

import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import io.kubernetes.client.ApiException;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelCreationPlugin;
import org.iac2.util.KubernetesTestUtil;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class KubernetesModelCreationPluginTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(KubernetesModelCreationPluginTest.class);

    private static final String kubeConfigPath = "D:\\UNI_Courses\\Research_Project\\config";

    private static final String namespace = "pet-clinic";

    @Test
    public void testReconstruction() throws FileNotFoundException, ApiException {
        ModelCreationPlugin plugin = KubernetesTestUtil.getKubernetesModelCreationPlugin();
        assertNotNull(plugin);
        assertEquals("kubernetes-model-creation-plugin", plugin.getIdentifier());

        ProductionSystem productionSystem = KubernetesTestUtil.createProductionSystem(kubeConfigPath,namespace);

        InstanceModel instanceModel = plugin.reconstructInstanceModel(productionSystem);
        LOGGER.info("Reconstructed edmm instance model:");
        StringWriter writer = new StringWriter();
        instanceModel.getDeploymentModel().getGraph().generateYamlOutput(writer);
        LOGGER.info(writer.toString());

        Set<RootComponent> comps = instanceModel.getDeploymentModel().getComponents();
        Set<RootRelation> rels = instanceModel.getDeploymentModel().getRelations();

//        assertEquals(applicationInstance.getNodeInstances().size(), comps.size());
//        assertEquals(applicationInstance.getRelationInstances().size(), rels.size());
//
//        // ensure all edmm components have corresponding node instances
//        assertEquals(applicationInstance.getNodeInstances().size(), comps
//                .stream()
//                .filter(c -> applicationInstance.getNodeInstances()
//                        .stream()
//                        .anyMatch(i -> i.getTemplate().equals(c.getName())))
//                .count());
//
//        // ensure all edmm components have correct outgoing relations
//        assertEquals(applicationInstance.getNodeInstances().size(), comps
//                .stream()
//                .filter(c -> applicationInstance.getRelationInstances()
//                        .stream()
//                        // find the node instance that corresponds to the source of the current relation
//                        // then compare its template id with the name of the current edmm component
//                        .filter(r -> findNodeInstanceById(r.getSourceId(), applicationInstance)
//                                .getTemplate()
//                                .equals(c.getName()))
//                        .count() == c.getRelations().size())
//                .count());
//
//        assertEquals(applicationInstance.getRelationInstances().size(), rels.stream().filter(r -> {
//            for (RelationInstance relationInstance : applicationInstance.getRelationInstances()) {
//                if (findNodeInstanceById(relationInstance.getTargetId(), applicationInstance).getTemplate().equals(r.getTarget())) {
//                    return true;
//                }
//            }
//            return false;
//        }).count());
    }

}
