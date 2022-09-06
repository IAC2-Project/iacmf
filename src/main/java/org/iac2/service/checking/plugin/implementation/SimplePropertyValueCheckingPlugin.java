package org.iac2.service.checking.plugin.implementation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.edmm.model.support.ModelEntity;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.common.model.compliancerule.parameter.ComplianceRuleParameter;
import org.iac2.service.checking.common.exception.ComplianceRuleTypeNotSupportedException;
import org.iac2.service.checking.common.interfaces.ComplianceRuleCheckingPlugin;

public class SimplePropertyValueCheckingPlugin implements ComplianceRuleCheckingPlugin {
    final static String ACCEPTED_COMPLIANCE_RULE_TYPE = "ensure-property-value";
    final static String PLUGIN_ID = "property-value-checker-plugin";

    // expected keys in the compliance rule parameter assignments
    static final String ENTITY_ID_KEY = "entity-id";
    static final String PROPERTY_NAME_KEY = "property-name";
    static final String PROPERTY_VALUE_KEY = "property-value";

    //special entity ids
    static final String ENTITY_ID_FOR_INSTANCE_MODEL = "<instance-model>";

    // issue types
    static final String WRONG_PROPERTY_VALUE_ISSUE_TYPE = "wrong-property-value";
    static final String MISSING_PROPERTY_ISSUE_TYPE = "missing-property";
    static final String MISSING_ENTITY_ISSUE_TYPE = "missing-entity";

    @Override
    public boolean isSuitableForComplianceRule(ComplianceRule complianceRule) {
        return ACCEPTED_COMPLIANCE_RULE_TYPE.equals(complianceRule.getType());
    }

    @Override
    public String getIdentifier() {
        return PLUGIN_ID;
    }

    @Override
    public Collection<ComplianceIssue> findIssues(InstanceModel instanceModel, ComplianceRule rule) {
        if (!isSuitableForComplianceRule(rule)) {
            throw new ComplianceRuleTypeNotSupportedException(rule.getType());
        }

        String entityId = null;
        String propertyName = null;
        String propertyValue = null;

        if (rule.getParameterAssignments() != null) {
            for (ComplianceRuleParameter p : rule.getParameterAssignments()) {
                switch (p.getName()) {
                    case ENTITY_ID_KEY -> entityId = p.getValueAsString();
                    case PROPERTY_NAME_KEY -> propertyName = p.getValueAsString();
                    case PROPERTY_VALUE_KEY -> propertyValue = p.getValueAsString();
                }
            }
        }

        if (entityId == null || propertyValue == null || propertyName == null) {
            StringBuilder strb = new StringBuilder();
            strb.append("Missing needed parameter assignments!");
            strb.append(System.lineSeparator());
            strb.append(ENTITY_ID_KEY).append("=").append(entityId);
            strb.append(System.lineSeparator());
            strb.append(PROPERTY_NAME_KEY).append("=").append(propertyName);
            strb.append(System.lineSeparator());
            strb.append(PROPERTY_VALUE_KEY).append("=").append(propertyValue);

            throw new IllegalArgumentException(strb.toString());
        }

        String actualValue = null;
        boolean propertyFound = false;
        boolean entityFound = false;

        // check if the property should be found at the instance model level or in a specific entity
        if (entityId.equals(ENTITY_ID_FOR_INSTANCE_MODEL)) {
            entityFound = true;
            if (instanceModel.getProperties().containsKey(propertyName)) {
                propertyFound = true;
                actualValue = instanceModel.getProperties().get(propertyName);
            }
        } else {
            // let's search for the entity

            if (instanceModel.getDeploymentModel() != null) {
                List<ModelEntity> allEntities = new ArrayList<>(instanceModel.getDeploymentModel().getComponents());
                allEntities.addAll(instanceModel.getDeploymentModel().getRelations());

                for (ModelEntity entity : allEntities) {
                    String id = entity.getId();

                    if (id.equals(entityId)) {
                        entityFound = true;
                        propertyFound = entity.getProperty(propertyName).isPresent();

                        if (propertyFound) {
                            actualValue = entity.getProperty(propertyName).get().getValue();
                        }

                        break;
                    }
                }
            }
        }

        if (!entityFound) {
            Map<String, String> issueProperties = new HashMap<>();
            issueProperties.put("entity-id", entityId);

            return List.of(new ComplianceIssue(
                    "An entity is missing from the instance model",
                    rule,
                    MISSING_ENTITY_ISSUE_TYPE,
                    issueProperties
            ));
        }

        if (!propertyFound) {
            Map<String, String> issueProperties = new HashMap<>();
            issueProperties.put("property-name", propertyName);

            return List.of(new ComplianceIssue(
                    "A property is missing from the instance model",
                    rule,
                    MISSING_PROPERTY_ISSUE_TYPE,
                    issueProperties
            ));
        }

        if (!propertyValue.equals(actualValue)) {
            Map<String, String> issueProperties = new HashMap<>();
            issueProperties.put("property-name", propertyName);
            issueProperties.put("expected-value", propertyValue);
            issueProperties.put("actual-value", actualValue);

            return List.of(new ComplianceIssue(
                    "A property value has an unexpected value.",
                    rule,
                    WRONG_PROPERTY_VALUE_ISSUE_TYPE,
                    issueProperties
            ));
        }

        return Collections.emptyList();
    }

}
