package org.iac2.api.controller;

import java.util.ArrayList;
import java.util.Collection;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.EnumUtils;
import org.iac2.api.model.PluginPojo;
import org.iac2.common.Plugin;
import org.iac2.common.model.PluginType;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.entity.compliancejob.issue.ComplianceIssueEntity;
import org.iac2.entity.compliancerule.ComplianceRuleEntity;
import org.iac2.repository.compliancejob.ComplianceIssueRepository;
import org.iac2.repository.compliancerule.ComplianceRuleRepository;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelCreationPlugin;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelEnhancementPlugin;
import org.iac2.service.architecturereconstruction.plugin.manager.ArchitectureReconstructionPluginManager;
import org.iac2.service.checking.common.interfaces.ComplianceRuleCheckingPlugin;
import org.iac2.service.checking.plugin.manager.ComplianceRuleCheckingPluginManager;
import org.iac2.service.fixing.plugin.manager.IssueFixingPluginManager;
import org.iac2.service.utility.EntityToPojo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "plugins")
@Tag(name = "plugin")
public class PluginController {
    private final ArchitectureReconstructionPluginManager arPluginManager;
    private final ComplianceRuleCheckingPluginManager checkingPluginManager;
    private final IssueFixingPluginManager fixingPluginManager;

    private final ComplianceRuleRepository complianceRuleRepository;

    private final ComplianceIssueRepository complianceIssueRepository;

    public PluginController(ArchitectureReconstructionPluginManager arPluginManager,
                            ComplianceRuleCheckingPluginManager checkingPluginManager,
                            IssueFixingPluginManager issueFixingPluginManager,
                            ComplianceRuleRepository complianceRuleRepository,
                            ComplianceIssueRepository complianceIssueRepository) {
        this.arPluginManager = arPluginManager;
        this.checkingPluginManager = checkingPluginManager;
        this.fixingPluginManager = issueFixingPluginManager;
        this.complianceRuleRepository = complianceRuleRepository;
        this.complianceIssueRepository = complianceIssueRepository;
    }

    private static PluginPojo createPluginPojo(Plugin plugin) {
        PluginType type;

        // todo add more cases when new plugin types are implemented
        if (plugin instanceof ModelCreationPlugin) {
            type = PluginType.MODEL_CREATION;
        } else if (plugin instanceof ModelEnhancementPlugin) {
            type = PluginType.MODEL_REFINEMENT;
        } else if (plugin instanceof ComplianceRuleCheckingPlugin) {
            type = PluginType.ISSUE_CHECKING;
        } else {
            type = PluginType.ISSUE_FIXING;
        }

        return new PluginPojo(plugin.getIdentifier(), type, plugin.getRequiredConfigurationEntryNames(), plugin.getConfigurationEntries());
    }

    @GetMapping
    @Operation(summary = "Retrieves all plugins that fulfill certain conditions. If no conditions are provided, all plugins are returned.",
            responses = {
                    @ApiResponse(
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = PluginPojo.class)))
                    ),
                    @ApiResponse(responseCode = "400", description = "The plugin type is malformed."),
                    @ApiResponse(responseCode = "404", description = "The compliance rule id or issue id cannot be found.")
            })
    public ResponseEntity<Collection<PluginPojo>> getAllPlugins(
            @Parameter(description = "(filter) The type of the plugins to be retrieved. Must be one of the PluginTypeEnums. This parameter is mandatory if any of the other parameters are not null.")
            @RequestParam(required = false)
            String pluginType,
            @Parameter(description = "(filter) The name of the IaC technology with which the plugins must be compatible. Only for model creation and issue fixing plugins.")
            @RequestParam(required = false)
            String iacTechnology,
            @Parameter(description = "(filter) The id of the compliance rule with which the plugins must be compatible. Only for compliance checking plugins.")
            @RequestParam(required = false)
            Long complianceRuleId,
            @Parameter(description = "(filter) The id of the compliance issue with which the plugins must be compatible. Only for issue fixing plugins.")
            @RequestParam(required = false)
            Long issueId) {
        Collection<PluginPojo> result = new ArrayList<>();

        if (pluginType == null) {
            this.arPluginManager.getAll().forEach(p -> result.add(createPluginPojo(p)));
            this.checkingPluginManager.getAll().forEach(p -> result.add(createPluginPojo(p)));
            this.fixingPluginManager.getAll().forEach(p -> result.add(createPluginPojo(p)));
        } else {
            PluginType type = EnumUtils.getEnum(PluginType.class, pluginType);

            if (type == null) {
                return ResponseEntity.badRequest().build();
            }

            if (complianceRuleId != null) {
                if (!this.complianceIssueRepository.existsById(complianceRuleId)) {
                    return ResponseEntity.notFound().build();
                }
            }

            if (issueId != null) {
                if (!this.complianceIssueRepository.existsById(issueId)) {
                    return ResponseEntity.notFound().build();
                }
            }

            switch (type) {
                case MODEL_CREATION -> this.arPluginManager
                        .getAll()
                        .stream()
                        .filter(p -> p instanceof ModelCreationPlugin)
                        .filter(p -> iacTechnology == null || ((ModelCreationPlugin) p).isIaCTechnologySupported(iacTechnology))
                        .forEach(p -> result.add(createPluginPojo(p)));

                case MODEL_REFINEMENT -> this.arPluginManager
                        .getAll()
                        .stream()
                        .filter(p -> p instanceof ModelEnhancementPlugin)
                        .forEach(p -> result.add(createPluginPojo(p)));
                case ISSUE_CHECKING -> this.checkingPluginManager
                        .getAll()
                        .stream()
                        .filter(p -> complianceRuleId == null || p.isSuitableForComplianceRule(getComplianceRule(complianceRuleId)))
                        .forEach(p -> result.add(createPluginPojo(p)));

                case ISSUE_FIXING -> this.fixingPluginManager
                        .getAll()
                        .stream()
                        .filter(p -> issueId == null ||
                                p.isSuitableForIssue(getComplianceIssue(complianceRuleId)))
                        .filter(p -> iacTechnology == null ||
                                p.isIaCTechnologySupported(iacTechnology))
                        .forEach(p -> result.add(createPluginPojo(p)));
                default -> {
                    return ResponseEntity.badRequest().build();
                }
            }
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "gets a plugin by its identifier",
            responses = {
                    @ApiResponse(
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PluginPojo.class))),
                    @ApiResponse(responseCode = "404", description = "Plugin not found")
            })
    public ResponseEntity<PluginPojo> getPlugin(@PathVariable String id) {
        Plugin plugin;

        if (this.arPluginManager.modelCreationPluginExists(id)) {
            plugin = this.arPluginManager.getModelCreationPlugin(id);
        } else if (this.arPluginManager.modelRefinementPluginExists(id)) {
            plugin = this.arPluginManager.getModelEnhancementPlugin(id);
        } else if (this.checkingPluginManager.pluginExists(id)) {
            plugin = this.checkingPluginManager.getPlugin(id);
        } else if (this.fixingPluginManager.pluginExists(id)) {
            plugin = this.fixingPluginManager.getPlugin(id);
        } else {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(createPluginPojo(plugin));
    }

    private ComplianceRule getComplianceRule(Long id) {
        ComplianceRuleEntity crEntity = this.complianceRuleRepository.findById(id).orElseThrow();

        return EntityToPojo.transformComplianceRule(crEntity, new ArrayList<>());
    }

    private ComplianceIssue getComplianceIssue(Long id) {
        ComplianceIssueEntity issueEntity = this.complianceIssueRepository.findById(id).orElseThrow();
        return EntityToPojo.transformIssue(issueEntity);
    }
}
