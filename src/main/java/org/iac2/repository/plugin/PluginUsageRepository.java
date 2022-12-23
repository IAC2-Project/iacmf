package org.iac2.repository.plugin;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.iac2.entity.plugin.PluginUsageEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "plugin-usages")
@Tag(name = "plugin-usage")
public interface PluginUsageRepository extends CrudRepository<PluginUsageEntity, Long> {
}
