package org.iac2.repository.compliancejob;

import org.iac2.entity.compliancejob.trigger.TriggerEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TriggerRepository extends CrudRepository<TriggerEntity, Long> {
    @Query("""
            SELECT t FROM TriggerEntity t
            JOIN t.complianceJobs j
            ON j.id = :jobId
            """)
    List<TriggerEntity> findAllTriggersOfJob(@Param("jobId") Long jobId);

    List<TriggerEntity> findByIsDeleted(Boolean isDeleted);
}
