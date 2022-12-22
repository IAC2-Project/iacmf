package org.iac2.repository.configuration;

import org.iac2.entity.configuration.PluginConfigurationEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PluginConfigurationRepository extends CrudRepository<PluginConfigurationEntity, Long> {
    List<PluginConfigurationEntity> findByPluginIdentifier(String pluginIdentifier);
}
