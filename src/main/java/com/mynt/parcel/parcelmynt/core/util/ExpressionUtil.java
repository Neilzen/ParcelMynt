
package com.mynt.parcel.parcelmynt.core.util;

import com.mynt.parcel.parcelmynt.core.enumeration.CostExpressions;
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
    static BigDecimal executeCostExpression(Parcel parcel, BigDecimal cost, String expression) {
        log.info("Start - Execute Cost Expression Util");
        String mappedExpression = mapValuesToExpression(parcel, cost, expression);

        log.info("Parsed Expression: {}", mappedExpression);
        ExpressionParser expressionParser = new SpelExpressionParser();
        BigDecimal expressionResult = expressionParser.parseExpression(mappedExpression).getValue(BigDecimal.class).setScale(2, RoundingMode.HALF_UP);

        log.info("End - Execute Cost Expression Util. Result: {}", expressionResult);
        return expressionResult;
    }

    private static Map<CostExpressions, BigDecimal> mapValuesToVariables(Parcel parcel, BigDecimal cost) {
        Map<CostExpressions, BigDecimal> values = new HashMap<>();
        values.put(CostExpressions.WEIGHT, parcel.getWeight());
        values.put(CostExpressions.HEIGHT, parcel.getHeight());
        values.put(CostExpressions.LENGTH, parcel.getLength());
        values.put(CostExpressions.WIDTH, parcel.getWidth());
        values.put(CostExpressions.VOLUME, parcel.getVolume());
        values.put(CostExpressions.COST, cost);
        return values;
    }

    private static String mapValuesToExpression(Parcel parcel, BigDecimal cost, String expression) {

        for (Map.Entry<CostExpressions, BigDecimal> costExpressionEntry : mapValuesToVariables(parcel, cost).entrySet()) {
            expression = expression.toUpperCase().replace(costExpressionEntry.getKey().name(), costExpressionEntry.getValue().toPlainString());
        }

        return expression;
    }


}
