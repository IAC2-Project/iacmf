package org.iac2.repository.configuration;

import java.util.List;

import org.iac2.entity.configuration.PluginConfigurationEntity;
import org.springframework.data.repository.CrudRepository;

public interface PluginConfigurationRepository extends CrudRepository<PluginConfigurationEntity, Long> {
    List<PluginConfigurationEntity> findByPluginIdentifier(String pluginIdentifier);
}
