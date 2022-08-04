package org.iac2.repositories.productionsystem;

import java.util.List;

import org.iac2.entity.productionsystem.ProductionSystemEntity;
import org.springframework.data.repository.CrudRepository;

public interface ProductionSystemRepository extends CrudRepository<ProductionSystemEntity, Long> {
    List<ProductionSystemEntity> findByIsDeleted(Boolean isDeleted);
}
