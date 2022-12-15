package org.iac2.api.controller;

import java.io.StringWriter;
import java.util.Optional;

import org.iac2.api.model.InstanceModelPojo;
import org.iac2.common.model.InstanceModel;
import org.iac2.entity.architecturereconstruction.ModelEnhancementStrategyEntity;
import org.iac2.entity.productionsystem.ProductionSystemEntity;
import org.iac2.repository.compliancejob.ModelEnhancementStrategyRepository;
import org.iac2.repository.productionsystem.ProductionSystemRepository;
import org.iac2.service.architecturereconstruction.service.ArchitectureReconstructionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("architectural-reconstruction")
public class ArchitecturalReconstructionController {
    private final ArchitectureReconstructionService service;
    private final ProductionSystemRepository productionSystemRepository;
    private final ModelEnhancementStrategyRepository modelEnhancementStrategyRepository;


    ArchitecturalReconstructionController(ArchitectureReconstructionService service,
                                          ProductionSystemRepository productionSystemRepository,
                                          ModelEnhancementStrategyRepository enhancementStrategyRepository) {
        this.service = service;
        this.productionSystemRepository = productionSystemRepository;
        this.modelEnhancementStrategyRepository = enhancementStrategyRepository;
    }

    @PostMapping(path = "architecture-reconstruction/reconstruct" )
    ResponseEntity<InstanceModelPojo> reconstructAndEnhanceInstanceModel(@RequestParam Long productionSystemId,
                                                                         @RequestParam Long modelEnhancementStrategyId) {

        Optional<ProductionSystemEntity> productionSystem =
                this.productionSystemRepository.findById(productionSystemId);
        Optional<ModelEnhancementStrategyEntity> enhancementStrategy =
                this.modelEnhancementStrategyRepository.findById(modelEnhancementStrategyId);

        if(productionSystem.isEmpty() || enhancementStrategy.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        InstanceModel model = this.service.reconstructArchitectureForProductionSystem(productionSystem.get());
        this.service.enhanceArchitectureForComplianceJob(enhancementStrategy.get(), productionSystem.get(), model);
        StringWriter writer = new StringWriter();
        model.getDeploymentModel().getGraph().generateYamlOutput(writer);
        InstanceModelPojo result = new InstanceModelPojo(writer.toString(), model.getProperties());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
