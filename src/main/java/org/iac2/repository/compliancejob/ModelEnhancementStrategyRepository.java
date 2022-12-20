package org.iac2.repository.compliancejob;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.iac2.entity.architecturereconstruction.ModelEnhancementStrategyEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "model-enhancement-strategies")
@Tag(name = "model-enhancement-strategy")
public interface ModelEnhancementStrategyRepository extends CrudRepository<ModelEnhancementStrategyEntity, Long> {
}