package org.iac2.api.controller;

import java.util.Collection;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.EnumUtils;
import org.iac2.common.model.compliancejob.execution.ExecutionStatus;
import org.iac2.common.model.compliancejob.execution.ExecutionStep;
import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.compliancejob.execution.ExecutionEntity;
import org.iac2.repository.compliancejob.ComplianceJobRepository;
import org.iac2.repository.compliancejob.ExecutionRepository;
import org.iac2.service.execution.ExecutionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("executions")
@Tag(name = "execution")
public class ExecutionController {
    private final ExecutionService service;
    private final ComplianceJobRepository complianceJobRepository;
    private final ExecutionRepository executionRepository;

    ExecutionController(ExecutionService service,
                        ComplianceJobRepository complianceJobRepository,
                        ExecutionRepository executionRepository) {
        this.service = service;
        this.complianceJobRepository = complianceJobRepository;
        this.executionRepository = executionRepository;
    }

    @PostMapping
    public ResponseEntity<ExecutionEntity> executeComplianceJob(
            @RequestParam Long complianceJobId,
            @RequestParam Boolean isBatchFixing) {
        try {
            ComplianceJobEntity jobEntity = this.complianceJobRepository.findById(complianceJobId).orElseThrow();
            ExecutionEntity execution = this.service.createNewExecution(jobEntity);
            this.service.runComplianceJobExecution(execution, isBatchFixing);

            return ResponseEntity.accepted().body(execution);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<Collection<ExecutionEntity>> getAllExecutions() {
        Collection<ExecutionEntity> result = IterableUtils.toList(this.executionRepository.findAll());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/findByComplianceJob")
    public ResponseEntity<Collection<ExecutionEntity>> getAllExecutionsOfJob(@RequestParam Long complianceJobId) {
        try {
            ComplianceJobEntity job = this.complianceJobRepository.findById(complianceJobId).orElseThrow();
            Collection<ExecutionEntity> result = this.executionRepository.findByComplianceJob(job);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/findByStatus")
    public ResponseEntity<Collection<ExecutionEntity>> getAllExecutionsOfStatus(@RequestParam String status) {
        ExecutionStatus statusEnum = EnumUtils.getEnumIgnoreCase(ExecutionStatus.class, status);

        if (statusEnum != null) {
            Collection<ExecutionEntity> result = this.executionRepository.findByStatus(statusEnum);
            return ResponseEntity.ok(result);
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/findByStep")
    public ResponseEntity<Collection<ExecutionEntity>> getAllExecutionsOfStep(@RequestParam String step) {
        ExecutionStep stepEnum = EnumUtils.getEnumIgnoreCase(ExecutionStep.class, step);

        if (stepEnum != null) {
            Collection<ExecutionEntity> result = this.executionRepository.findByCurrentStep(stepEnum);
            return ResponseEntity.ok(result);
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExecutionEntity> getExecutionStatus(@PathVariable Long id) {
        try {
            ExecutionEntity execution = this.executionRepository.findById(id).orElseThrow();
            return ResponseEntity.ok(execution);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
