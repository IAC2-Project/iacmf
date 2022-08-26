package org.iac2.repositories.productionsystem;

import org.iac2.entity.productionsystem.ProductionSystemEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductionSystemRepository extends CrudRepository<ProductionSystemEntity, Long> {
    List<ProductionSystemEntity> findByIsDeleted(Boolean isDeleted);
}
