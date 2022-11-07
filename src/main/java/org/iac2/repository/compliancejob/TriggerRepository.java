package org.iac2.repository.compliancejob;

import java.util.List;

import org.iac2.entity.compliancejob.trigger.TriggerEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "triggers")
public interface TriggerRepository extends CrudRepository<TriggerEntity, Long> {
    @Query("""
            SELECT t FROM TriggerEntity t
            JOIN t.complianceJobs j
            ON j.id = :jobId
            """)
    List<TriggerEntity> findAllTriggersOfJob(@Param("jobId") Long jobId);

    List<TriggerEntity> findByIsDeleted(Boolean isDeleted);
}
