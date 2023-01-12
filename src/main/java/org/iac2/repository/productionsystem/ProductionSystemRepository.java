package org.iac2.repository.productionsystem;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.iac2.entity.productionsystem.ProductionSystemEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;

@RepositoryRestResource(path = "production-systems")
@Tag(name = "production-system")
@CrossOrigin( origins = "*" , allowedHeaders = "*" , methods = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.POST, RequestMethod.PATCH, RequestMethod.OPTIONS})
public interface ProductionSystemRepository extends CrudRepository<ProductionSystemEntity, Long> {
    List<ProductionSystemEntity> findByIsDeleted(Boolean isDeleted);
}
