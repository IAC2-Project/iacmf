package org.iac2.service.utility;

import javax.persistence.EntityManager;

import org.iac2.common.Plugin;
import org.iac2.common.PluginManager;
import org.iac2.entity.compliancejob.execution.ExecutionEntity;
import org.iac2.entity.plugin.PluginConfigurationEntity;
import org.iac2.entity.plugin.PluginUsageEntity;
import org.iac2.entity.plugin.PluginUsageInstanceEntity;

public class PluginConfigurationHelper {

    public static Plugin instantiatePlugin(PluginUsageEntity usageEntity,
                                           ExecutionEntity execution,
                                           EntityManager entityManager,
                                           PluginManager pluginManager) {
        entityManager.getTransaction().begin();
        PluginUsageInstanceEntity usageInstanceEntity = new PluginUsageInstanceEntity(usageEntity, execution);
        entityManager.persist(usageInstanceEntity);

        try {
            for (PluginConfigurationEntity configurationEntity : usageEntity.getPluginConfiguration()) {
                PluginConfigurationEntity current =
                        new PluginConfigurationEntity(configurationEntity.getKey(), configurationEntity.getValue(), usageInstanceEntity);
                entityManager.persist(current);
            }
        } catch (RuntimeException e) {
            entityManager.getTransaction().rollback();
            throw e;
        }

        entityManager.getTransaction().commit();
        Plugin plugin = pluginManager.getPlugin(usageEntity.getPluginIdentifier());

        for (PluginConfigurationEntity configurationEntity : usageEntity.getPluginConfiguration()) {
            plugin.setConfigurationEntry(configurationEntity.getKey(), configurationEntity.getValue());
        }

        return plugin;
    }
}
