package org.iac2.repository.configuration;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.iac2.entity.configuration.PluginConfigurationEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "plugin-configurations")
@Tag(name = "plugin-configuration")
public interface PluginConfigurationRepository extends CrudRepository<PluginConfigurationEntity, Long> {
    List<PluginConfigurationEntity> findByPluginIdentifier(String pluginIdentifier);
}
