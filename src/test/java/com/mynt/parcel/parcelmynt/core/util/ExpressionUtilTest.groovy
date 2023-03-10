package com.mynt.parcel.parcelmynt.core.util

import com.mynt.parcel.parcelmynt.core.exception.ParcelException
import com.mynt.parcel.parcelmynt.core.model.Parcel
import spock.lang.Specification
import spock.lang.Unroll

import java.math.RoundingMode

class ExpressionUtilTest extends Specification {

    @Unroll
    void 'Test Cost Expression - #scope'() {
        given:
        def parcel = new Parcel(
                weight: weight,
                height: height,
                width: width,
                length: length
        )

        when:
        def result = ExpressionUtil.executeCostExpression(parcel, new BigDecimal(cost), expression)

        then:
        new BigDecimal(output).setScale(2, RoundingMode.HALF_UP) == result

        where:
        scope       | expression                                  | weight | height | width | length | cost | output
        'basic'     | 'width + height + length'                   | 10.5   | 15.8   | 3.67  | 4.44   | 0.06 | 23.91
        'c x w'     | 'cost * weight'                             | 10.5   | 15.8   | 3.67  | 4.44   | 20   | 210
        'c x v'     | 'cost * volume'                             | 10.5   | 15.8   | 3.67  | 4.44   | 0.03 | 7.72
        'c x v'     | 'cost * (width + height + length)'          | 10.5   | 15.8   | 3.67  | 4.44   | 0.04 | 0.96
        'c x v / w' | '(cost * (width + height + length))/weight' | 10.5   | 15.8   | 3.67  | 4.44   | 0.04 | 0.09
    }

    void 'Test Cost Expression - empty expression'() {
        given:
        def parcel = new Parcel(
                weight: 10.5,
                height: 15.8,
                width: 3.67,
                length: 4.44
        )
        def expression = ""

        when:
        def result = ExpressionUtil.executeCostExpression(parcel, new BigDecimal(0.06), expression)

        then:
        def exception = thrown(ParcelException)
        'Expression for computing cost is empty' == exception.getMessage()
    }

    void 'Test Cost Expression - invalid expression'() {
        given:
        def parcel = new Parcel(
                weight: 10.5,
                height: 15.8,
                width: 3.67,
                length: 4.44
        )
        def expression = "wrong + wrong"

        when:
        def result = ExpressionUtil.executeCostExpression(parcel, new BigDecimal(0.06), expression)

        then:
        def exception = thrown(ParcelException)
        'Failed to execute cost expression' == exception.getMessage()
    }

    @Unroll
    void 'Test Condition Expression - #scope'() {
        given:
        def parcel = new Parcel(
                weight: weight,
                height: height,
                width: width,
                length: length
        )

        when:
        def result = ExpressionUtil.executeConditionExpression(expression, parcel, new BigDecimal(limit))

        then:
        output == result

        where:
        scope       | expression                        | weight | height | width | length | limit | output
        'reject'    | 'weight > limit'                  | 10.5   | 15.8   | 3.67  | 4.44   | 10    | true
        'heavy'     | 'weight > limit'                  | 4.99   | 15.8   | 3.67  | 4.44   | 5     | false
        'small'     | 'volume < limit'                  | 10.5   | 15.8   | 3.67  | 21     | 1500  | true
        'medium'    | 'volume < limit'                  | 10.5   | 15.8   | 15    | 57     | 2500  | false
        'mixed'     | 'weight > limit && volume < 1500' | 10.5   | 15.8   | 3.67  | 21     | 0.04  | true
        'condition' | 'true'                            | 10.5   | 15.8   | 3.67  | 21     | 0.04  | true
        'empty'     | ''                                | 10.5   | 15.8   | 3.67  | 21     | 0.04  | true
    }

    void 'Test Condition Expression - invalid expression'() {
        given:
        def parcel = new Parcel(
                weight: 10.5,
                height: 15.8,
                width: 3.67,
                length: 4.44
        )
        def expression = "wrong + wrong"

        when:
        def result = ExpressionUtil.executeConditionExpression(expression, parcel, new BigDecimal(0.04))

        then:
        def exception = thrown(ParcelException)
        'Failed to execute condition expression' == exception.getMessage()
    }

}
