
package com.mynt.parcel.parcelmynt.core.util;

import com.mynt.parcel.parcelmynt.core.enumeration.VariableExpressions;
import com.mynt.parcel.parcelmynt.core.exception.ParcelException;
import com.mynt.parcel.parcelmynt.core.model.Parcel;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.StringUtils;

@Slf4j
public class ExpressionUtil {

    private static final ExpressionParser expressionParser = new SpelExpressionParser();

    public static BigDecimal executeCostExpression(Parcel parcel, BigDecimal cost, String expression) {
        log.info("Start - Execute Cost Expression Util");

        if (!StringUtils.hasLength(expression)) {
            throw new ParcelException("Expression for computing cost is empty");
        }

        String mappedExpression = mapValuesToCostExpression(parcel, cost, expression);

        log.info("Parsed Expression: {}", mappedExpression);
        BigDecimal expressionResult;
        try {
            expressionResult = expressionParser
                    .parseExpression(mappedExpression)

                    .getValue(BigDecimal.class)
                    .setScale(2, RoundingMode.HALF_UP);
        } catch (SpelEvaluationException e) {
            log.error("Failed to execute cost expression due to following expression {}", expression, e);
            throw new ParcelException("Failed to execute cost expression", e);
        }


        log.info("End - Execute Cost Expression Util. Result: {}", expressionResult);
        return expressionResult;
    }

    public static Boolean executeConditionExpression(String expression, Parcel parcel, BigDecimal limit) {
        log.info("Start - Exucute Condition Expression Util");
        if (!StringUtils.hasLength(expression)) {
            return true;
        }
        String mappedExpression = mapValuesToConditionExpression(parcel, limit, expression);

        log.info("Parsed Expression: {}", mappedExpression);
        Boolean result;
        try {
            result = expressionParser
                    .parseExpression(mappedExpression)
                    .getValue(Boolean.class);
        } catch(SpelEvaluationException e) {
            log.error("Failed to execute condition expression due to following expression {}", expression, e);
            throw new ParcelException("Failed to execute condition expression", e);
        }

        log.info("End - Exucute Condition Expression Util");
        return result;
    }

    private static String mapValuesToCostExpression(Parcel parcel, BigDecimal cost, String expression) {
        for (Map.Entry<VariableExpressions, BigDecimal> costExpressionEntry : mapValuesToCostVariables(parcel, cost).entrySet()) {
            expression = expression.toUpperCase().replace(costExpressionEntry.getKey().name(), costExpressionEntry.getValue().toPlainString());
        }

        return expression;
    }

    private static String mapValuesToConditionExpression(Parcel parcel, BigDecimal limit, String expression) {
        for (Map.Entry<VariableExpressions, BigDecimal> costExpressionEntry : mapValuesToConditionVariables(parcel, limit).entrySet()) {
            expression = expression.toUpperCase().replace(costExpressionEntry.getKey().name(), costExpressionEntry.getValue().toPlainString());
        }

        return expression;
    }

    private static Map<VariableExpressions, BigDecimal> mapValuesToCostVariables(Parcel parcel, BigDecimal cost) {
        Map<VariableExpressions, BigDecimal> values = new HashMap<>();
        mapDimensions(values, parcel);
        values.put(VariableExpressions.COST, cost);
        return values;
    }

    private static Map<VariableExpressions, BigDecimal> mapDimensions(Map<VariableExpressions, BigDecimal> values, Parcel parcel) {
        values.put(VariableExpressions.WEIGHT, parcel.getWeight());
        values.put(VariableExpressions.HEIGHT, parcel.getHeight());
        values.put(VariableExpressions.LENGTH, parcel.getLength());
        values.put(VariableExpressions.WIDTH, parcel.getWidth());
        values.put(VariableExpressions.VOLUME, parcel.getVolume());
        return values;
    }

    private static Map<VariableExpressions, BigDecimal> mapValuesToConditionVariables(Parcel parcel, BigDecimal limit) {
        Map<VariableExpressions, BigDecimal> values = new HashMap<>();
        mapDimensions(values, parcel);
        values.put(VariableExpressions.LIMIT, limit);
        return values;
    }


}
