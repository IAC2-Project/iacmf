package org.iac2.repository.plugin;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.iac2.entity.plugin.PluginConfigurationEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "plugin-configurations")
@Tag(name = "plugin-configuration")
public interface PluginConfigurationRepository extends CrudRepository<PluginConfigurationEntity, Long> {
    List<PluginConfigurationEntity> findByPluginIdentifier(String pluginIdentifier);
}
