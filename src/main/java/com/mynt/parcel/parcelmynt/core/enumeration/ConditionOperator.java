package com.mynt.parcel.parcelmynt.core.enumeration;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ConditionOperator {

    GREATER_THAN(">"),
    GREATER_THAN_EQUAL(">="),
    LESS_THAN("<"),
    LESS_THAN_EQUAL("<="),
    EQUAL("=");

    private final String operator;

}
