package org.iac2.repository.compliancejob;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.iac2.entity.compliancejob.trigger.TriggerEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;

@RepositoryRestResource(path = "triggers")
@Tag(name = "trigger")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.POST, RequestMethod.PATCH, RequestMethod.OPTIONS})
public interface TriggerRepository extends CrudRepository<TriggerEntity, Long> {
    @Query("""
            SELECT t FROM TriggerEntity t
            JOIN t.complianceJobs j
            ON j.id = :jobId
            """)
    List<TriggerEntity> findAllTriggersOfJob(@Param("jobId") Long jobId);

    List<TriggerEntity> findByIsDeleted(Boolean isDeleted);

    @Override
    @RestResource(exported = false)
    void deleteById(Long aLong);

    @Override
    @RestResource(exported = false)
    void delete(TriggerEntity entity);

    @Override
    void deleteAllById(Iterable<? extends Long> longs);

    @Override
    @RestResource(exported = false)
    void deleteAll(Iterable<? extends TriggerEntity> entities);

    @Override
    @RestResource(exported = false)
    void deleteAll();
}
