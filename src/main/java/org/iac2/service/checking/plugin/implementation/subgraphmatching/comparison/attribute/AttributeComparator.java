package org.iac2.service.checking.plugin.implementation.subgraphmatching.comparison.attribute;

import io.github.edmm.model.Property;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.common.model.compliancerule.parameter.*;
import org.iac2.service.checking.plugin.implementation.subgraphmatching.model.SimpleProperty;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AttributeComparator {

    public static boolean evaluateAttribute(String expressionS, SimpleProperty property, ComplianceRule rule) throws EvaluationException {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        Map<String, Object> assignments = prepareAssignments(rule.getParameterAssignments());
        context.setVariables(assignments);
        RuleAttributeEvaluationContext<?> ruleContext = RuleAttributeEvaluationContext.forProperty(property);
        context.setRootObject(ruleContext);
        Expression expression = parser.parseExpression(expressionS);

        // the following statement might throw EvaluationException if the evaluation result is not a boolean value
        return Boolean.TRUE.equals(expression.getValue(context, Boolean.class));
    }

    public static boolean evaluateAttribute(String expressionS, Property property, ComplianceRule rule) throws EvaluationException {
        return evaluateAttribute(expressionS, new SimpleProperty(property), rule);
    }

    private static Map<String, Object> prepareAssignments(Collection<ComplianceRuleParameter> parameters) {
        Map<String, Object> result = new HashMap<>();
        String name;
        Object value;

        for (ComplianceRuleParameter parameter : parameters) {
            name = parameter.getName();

            if (parameter instanceof BooleanComplianceRuleParameter) {
                value = ((BooleanComplianceRuleParameter) parameter).isValue();
            } else if (parameter instanceof IntegerComplianceRuleParameter) {
                value = ((IntegerComplianceRuleParameter) parameter).getValue();
            } else if (parameter instanceof DoubleComplianceRuleParameter) {
                value = ((DoubleComplianceRuleParameter) parameter).getValue();
            } else if (parameter instanceof StringCollectionComplianceRuleParameter) {
                value = ((StringCollectionComplianceRuleParameter) parameter).getValue();
            } else {
                value = parameter.getValueAsString();
            }

            result.put(name, value);
        }

        return result;
    }
}
