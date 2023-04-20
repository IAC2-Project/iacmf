package org.iac2.util;

import com.google.common.collect.Maps;
import org.iac2.common.model.ProductionSystem;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelCreationPlugin;
import org.iac2.service.architecturereconstruction.plugin.factory.implementation.SimpleARPluginFactory;

import java.util.Map;

public class KubernetesTestUtil {

    public static ProductionSystem createProductionSystem(String kubeConfigPath, String namespace) {
        Map<String, String> prodProps = Maps.newHashMap();
        prodProps.put("kubeConfigPath", kubeConfigPath);
        prodProps.put("namespace", namespace);

        ProductionSystem productionSystem = new ProductionSystem("kubernetes", "petclinic-test", prodProps);
        return productionSystem;
    }

    public static ModelCreationPlugin getKubernetesModelCreationPlugin() {
        SimpleARPluginFactory instance = SimpleARPluginFactory.getInstance();
        ModelCreationPlugin plugin = instance.createModelCreationPlugin("kubernetes-model-creation-plugin");
        return plugin;
    }

}
