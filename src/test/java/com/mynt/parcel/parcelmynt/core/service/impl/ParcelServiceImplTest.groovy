package com.mynt.parcel.parcelmynt.core.service.impl

import com.mynt.parcel.parcelmynt.adapter.ParcelConditionAdapter
import com.mynt.parcel.parcelmynt.adapter.VoucherAdapter
import com.mynt.parcel.parcelmynt.api.dto.ParcelDTO
import com.mynt.parcel.parcelmynt.core.configuration.CoreTestConfiguration
import com.mynt.parcel.parcelmynt.core.entity.ParcelConditionEntity
import com.mynt.parcel.parcelmynt.core.exception.ParcelException
import com.mynt.parcel.parcelmynt.core.model.Parcel
import com.mynt.parcel.parcelmynt.core.service.ParcelService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

@ContextConfiguration(classes = [CoreTestConfiguration])
class ParcelServiceImplTest extends Specification {

    @Autowired
    ParcelService parcelService

    @Autowired
    VoucherAdapter voucherAdapter

    @Autowired
    ParcelConditionAdapter parcelConditionAdapter

    @Unroll
    def 'Test Compute Parcel Cost - #scope' () {
        given:
        def parcelDTO = new ParcelDTO(
                new BigDecimal(weight),
                new BigDecimal(height),
                new BigDecimal(width),
                new BigDecimal(length),
                voucherCode
        )

        mockGetAllActiveParcelConditionOrderedByPriority()

        when:
        def response = parcelService.calculateCost(parcelDTO)

        then:
        reject == response.reject
        new BigDecimal(grossCost) == response.grossCost
        new BigDecimal(netCost) == response.netCost

        where:
        scope    | weight | height  | width  | length | voucherCode | grossCost | netCost | reject
        'reject' | '51'   | '15.80' | '3.67' | '21'   | null        | '0.00'       | '0.00'   | true
        'heavy'  | '11'   | '15.80' | '3.67' | '21'   | null        | '220.00'     | '220.00'   | false
        'small'  | '10'   | '15.80' | '3.67' | '21'   | null        | '36.53'   | '36.53' | false
        'medium' | '10'   | '15.80' | '3.67' | '30'   | null        | '69.58'   | '69.58' | false
        'large'  | '10'   | '15.80' | '3.67' | '120'  | null        | '347.92'   | '347.92' | false
    }

    def 'Test Compute Parcel Cost - Condition does not exist or empty' () {
        given:
        def parcelDTO = new ParcelDTO(
                new BigDecimal('51'),
                new BigDecimal('15.80'),
                new BigDecimal('3.67'),
                new BigDecimal('30'),
                ''
        )

        parcelConditionAdapter.getAllActiveParcelConditionOrderedByPriority() >> []

        when:
        def response = parcelService.calculateCost(parcelDTO)

        then:
        def error = thrown(ParcelException)
        "No Valid condition found" == error.message
    }

    private mockGetAllActiveParcelConditionOrderedByPriority() {
        parcelConditionAdapter.getAllActiveParcelConditionOrderedByPriority() >>
                [
                        new ParcelConditionEntity(
                                0,
                                0,
                                'Reject',
                                'weight > limit',
                                new BigDecimal(50),
                                BigDecimal.ZERO,
                                '',
                                true,
                                true),
                        new ParcelConditionEntity(
                                0,
                                1,
                                'Heavy Parcel',
                                'weight > limit',
                                new BigDecimal(10),
                                new BigDecimal('20'),
                                'cost * weight',
                                false,
                                true),
                        new ParcelConditionEntity(
                                2,
                                3,
                                'Small Parcel',
                                'volume < limit',
                                new BigDecimal(1500),
                                new BigDecimal('0.03'),
                                'cost * volume',
                                false,
                                true),
                        new ParcelConditionEntity(
                                3,
                                4,
                                'Medium Parcel',
                                'volume < limit',
                                new BigDecimal(2500),
                                new BigDecimal('0.04'),
                                'cost * volume',
                                false,
                                true),
                        new ParcelConditionEntity(
                                4,
                                5,
                                'Large Parcel',
                                '',
                                null,
                                new BigDecimal('0.05'),
                                'cost * volume',
                                false,
                                true)
                ]
    }

}
