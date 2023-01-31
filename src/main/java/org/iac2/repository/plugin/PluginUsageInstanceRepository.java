package org.iac2.repository.plugin;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.iac2.entity.plugin.PluginUsageInstanceEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;

@RepositoryRestResource(path = "plugin-usage-instances")
@Tag(name = "plugin-usage-instance")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.POST, RequestMethod.PATCH, RequestMethod.OPTIONS})
public interface PluginUsageInstanceRepository extends CrudRepository<PluginUsageInstanceEntity, Long> {
}
