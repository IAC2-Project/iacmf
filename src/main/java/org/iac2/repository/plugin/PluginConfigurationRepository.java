package org.iac2.repository.plugin;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.iac2.entity.plugin.PluginConfigurationEntity;
import org.iac2.entity.plugin.PluginUsageEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;

@RepositoryRestResource(path = "plugin-configurations")
@Tag(name = "plugin-configuration")
@CrossOrigin( origins = "*" , allowedHeaders = "*" , methods = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.POST, RequestMethod.PATCH, RequestMethod.OPTIONS})
public interface PluginConfigurationRepository extends CrudRepository<PluginConfigurationEntity, Long> {
    List<PluginConfigurationEntity> findByPluginUsage(PluginUsageEntity pluginUsage);
}
