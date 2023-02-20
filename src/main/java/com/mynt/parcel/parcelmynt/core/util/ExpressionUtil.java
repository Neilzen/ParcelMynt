
package com.mynt.parcel.parcelmynt.core.util;

import com.mynt.parcel.parcelmynt.core.enumeration.VariableExpressions;
import com.mynt.parcel.parcelmynt.core.model.Parcel;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

@Slf4j
public class ExpressionUtil {

    private static final ExpressionParser expressionParser = new SpelExpressionParser();

    static BigDecimal executeCostExpression(Parcel parcel, BigDecimal cost, String expression) {
        log.info("Start - Execute Cost Expression Util");
        String mappedExpression = mapValuesToCostExpression(parcel, cost, expression);

        log.info("Parsed Expression: {}", mappedExpression);
        BigDecimal expressionResult = expressionParser
                .parseExpression(mappedExpression)
                .getValue(BigDecimal.class)
                .setScale(2, RoundingMode.HALF_UP);

        log.info("End - Execute Cost Expression Util. Result: {}", expressionResult);
        return expressionResult;
    }

    static Boolean executeConditionExpression(String expression, Parcel parcel, BigDecimal limit) {
        log.info("Start - Exucute Condition Expression Util");
        String mappedExpression = mapValuesToConditionExpression(parcel, limit, expression);

        log.info("Parsed Expression: {}", mappedExpression);
        Boolean result = expressionParser
                .parseExpression(mappedExpression)
                        .getValue(Boolean.class);

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

    private static Map<VariableExpressions, BigDecimal> mapValuesToConditionLimits(Map<VariableExpressions, BigDecimal> values, BigDecimal limit) {

        return values;
    }

    private static Map<VariableExpressions, BigDecimal> mapValuesToConditionVariables(Parcel parcel, BigDecimal limit) {
        Map<VariableExpressions, BigDecimal> values = new HashMap<>();
        mapDimensions(values, parcel);
        values.put(VariableExpressions.LIMIT, limit);
        return values;
    }




}
