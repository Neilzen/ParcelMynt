package com.mynt.parcel.parcelmynt.core.util

import com.mynt.parcel.parcelmynt.core.model.Parcel
import org.junit.jupiter.api.Test
import spock.lang.Specification
import spock.lang.Unroll

import java.math.RoundingMode

class ExpressionUtilTest extends Specification {

    @Test
    @Unroll
    void 'Test Cost Expression - #scope'() {
        given:
        def parcel = new Parcel(
                weight: new BigDecimal('10.5'),
                height: new BigDecimal('15.8'),
                width: new BigDecimal('3.67'),
                length: new BigDecimal('4.44')
        )

        when:
        def result = ExpressionUtil.executeCostExpression(parcel, new BigDecimal(cost), expression)

        then:
        new BigDecimal(output).setScale(2, RoundingMode.HALF_UP) == result

        where:
        scope   | expression                         | weight | height | width | length | cost | output
        'basic' | 'width + height + length'          | 10.5   | 15.8   | 3.67  | 4.44   | 0.06 | 23.91
        'basic' | 'cost * weight'                    | 10.5   | 15.8   | 3.67  | 4.44   | 20   | 210
        'basic' | 'cost * volume'                    | 10.5   | 15.8   | 3.67  | 4.44   | 0.03 | 7.72
        'basic' | 'cost * (width + height + length)' | 10.5   | 15.8   | 3.67  | 4.44   | 0.04 | 0.96
    }

}
