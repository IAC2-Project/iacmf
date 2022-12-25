package org.iac2.service.utility;

import javax.transaction.Transactional;

import org.iac2.common.Plugin;
import org.iac2.common.PluginFactory;
import org.iac2.entity.compliancejob.execution.ExecutionEntity;
import org.iac2.entity.plugin.PluginConfigurationEntity;
import org.iac2.entity.plugin.PluginUsageEntity;
import org.iac2.entity.plugin.PluginUsageInstanceEntity;
import org.iac2.repository.plugin.PluginConfigurationRepository;
import org.iac2.repository.plugin.PluginUsageInstanceRepository;
import org.springframework.stereotype.Service;

@Service
public class PluginConfigurationHelperService {
    private final PluginUsageInstanceRepository pluginUsageInstanceRepository;
    private final PluginConfigurationRepository pluginConfigurationRepository;

    public PluginConfigurationHelperService(PluginUsageInstanceRepository pluginUsageInstanceRepository,
                                            PluginConfigurationRepository pluginConfigurationRepository) {
        this.pluginUsageInstanceRepository = pluginUsageInstanceRepository;
        this.pluginConfigurationRepository = pluginConfigurationRepository;
    }

    @Transactional
    public Plugin instantiatePlugin(PluginUsageEntity usageEntity,
                                    ExecutionEntity execution,
                                    PluginFactory pluginManager) {

        PluginUsageInstanceEntity usageInstanceEntity = new PluginUsageInstanceEntity(usageEntity);
        execution.addPluginUsageInstance(usageInstanceEntity);
        pluginUsageInstanceRepository.save(usageInstanceEntity);
        
        for (PluginConfigurationEntity configurationEntity : usageEntity.getPluginConfiguration()) {
            PluginConfigurationEntity current =
                    new PluginConfigurationEntity(configurationEntity.getKey(), configurationEntity.getValue());
            usageInstanceEntity.addPluginConfiguration(current);
            pluginConfigurationRepository.save(current);
        }

        Plugin plugin = pluginManager.createPlugin(usageEntity.getPluginIdentifier());

        for (PluginConfigurationEntity configurationEntity : usageEntity.getPluginConfiguration()) {
            plugin.setConfigurationEntry(configurationEntity.getKey(), configurationEntity.getValue());
        }

        return plugin;
    }
}
