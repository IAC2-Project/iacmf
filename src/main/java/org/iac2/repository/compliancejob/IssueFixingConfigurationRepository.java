package org.iac2.repository.compliancejob;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.iac2.entity.compliancejob.issue.IssueFixingConfigurationEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "issue-fixing-configurations")
@Tag(name = "issue-fixing-configuration")
public interface IssueFixingConfigurationRepository extends CrudRepository<IssueFixingConfigurationEntity, Long> {
}
