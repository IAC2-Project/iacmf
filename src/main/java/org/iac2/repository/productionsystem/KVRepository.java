package org.iac2.repository.productionsystem;

import org.iac2.entity.KVEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface KVRepository extends CrudRepository<KVEntity, Long> {
    List<KVEntity> findByKey(String key);
}
