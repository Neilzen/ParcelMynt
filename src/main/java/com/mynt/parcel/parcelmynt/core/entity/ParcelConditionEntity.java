package com.mynt.parcel.parcelmynt.core.entity;

import com.mynt.parcel.parcelmynt.core.enumeration.ConditionOperator;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParcelConditionEntity {

    private int id;
    private Integer priority;
    private String ruleName;
    private String conditionExpression;
    private ConditionOperator conditionOperator;
    private BigDecimal conditionLimit;
    private BigDecimal cost;
    private String costExpression;
    private boolean isReject;

}
