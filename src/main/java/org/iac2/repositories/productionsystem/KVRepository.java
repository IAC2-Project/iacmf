package org.iac2.repositories.productionsystem;

import java.util.List;

import org.iac2.entity.productionsystem.KVEntity;
import org.springframework.data.repository.CrudRepository;

public interface KVRepository extends CrudRepository<KVEntity, Long> {
    List<KVEntity> findByKey(String key);
}
