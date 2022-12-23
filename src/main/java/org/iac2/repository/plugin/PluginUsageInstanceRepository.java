package org.iac2.repository.plugin;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.iac2.entity.plugin.PluginUsageInstanceEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "plugin-usage-instances")
@Tag(name = "plugin-usage-instance")
public interface PluginUsageInstanceRepository extends CrudRepository<PluginUsageInstanceEntity, Long> {
}
