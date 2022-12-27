package org.iac2.repository.productionsystem;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.iac2.entity.KVEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "key-values")
@Tag(name = "key-value")
public interface KVRepository extends CrudRepository<KVEntity, Long> {
    List<KVEntity> findByKey(String key);
}
