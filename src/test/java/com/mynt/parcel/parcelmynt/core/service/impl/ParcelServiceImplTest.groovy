package com.mynt.parcel.parcelmynt.core.service.impl

import com.mynt.parcel.parcelmynt.api.response.VoucherErrorResponse
import com.mynt.parcel.parcelmynt.api.response.VoucherResponse
import com.mynt.parcel.parcelmynt.core.adapter.ParcelConditionAdapter
import com.mynt.parcel.parcelmynt.core.adapter.VoucherAdapter
import com.mynt.parcel.parcelmynt.api.dto.ParcelDTO
import com.mynt.parcel.parcelmynt.core.configuration.CoreTestConfiguration
import com.mynt.parcel.parcelmynt.core.entity.ParcelConditionEntity
import com.mynt.parcel.parcelmynt.core.enumeration.VoucherStatus
import com.mynt.parcel.parcelmynt.core.exception.ParcelException
import com.mynt.parcel.parcelmynt.core.exception.VoucherNotFoundException
import com.mynt.parcel.parcelmynt.core.service.ParcelService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
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
    def 'Test Compute Parcel Cost - #scope'() {
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
        scope    | weight | height  | width  | length | voucherCode | grossCost | netCost  | reject
        'reject' | '51'   | '15.80' | '3.67' | '21'   | null        | '0.00'    | '0.00'   | true
        'heavy'  | '11'   | '15.80' | '3.67' | '21'   | null        | '220.00'  | '220.00' | false
        'small'  | '10'   | '15.80' | '3.67' | '21'   | null        | '36.53'   | '36.53'  | false
        'medium' | '10'   | '15.80' | '3.67' | '30'   | null        | '69.58'   | '69.58'  | false
        'large'  | '10'   | '15.80' | '3.67' | '120'  | null        | '347.92'  | '347.92' | false
    }

    def 'Test Compute Parcel Cost - Condition does not exist or empty'() {
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

    @Unroll
    def 'Text Compute Parcel Cost - #scope'() {
        given:
        def parcelDTO = new ParcelDTO(
                new BigDecimal('11'),
                new BigDecimal('15.80'),
                new BigDecimal('3.67'),
                new BigDecimal('21'),
                voucherCode
        )

        voucherAdapter.getVoucherDiscount(voucherCode) >>
                VoucherResponse.builder()
                        .voucherCode(voucherCode)
                        .discount(discount)
                        .isExpired(expired)
                        .voucherErrorResponse(voucherErrorResponse)
                        .build()

        mockGetAllActiveParcelConditionOrderedByPriority()

        when:
        def response = parcelService.calculateCost(parcelDTO)

        then:
        !response.reject
        grossCost == response.grossCost
        netCost == response.netCost

        def voucherResponse = response.getVoucherResponse()
        voucherCode == voucherResponse.getVoucherCode()
        discount == voucherResponse.getDiscount()
        expired == voucherResponse.expired
        (ObjectUtils.isEmpty(voucherErrorResponse) && ObjectUtils.isEmpty(voucherResponse.voucherErrorResponse)) || (
                !ObjectUtils.isEmpty(voucherErrorResponse)
                        && status == voucherResponse.voucherErrorResponse.status
                        && message == voucherResponse.voucherErrorResponse.errorMessage
        )


        where:
        scope                   | voucherCode       | discount                | expired | grossCost                | netCost                  | voucherErrorResponse                                                                     | status                         | message
        "Apply Valid Voucher"   | "VOUCHER_TEST"    | new BigDecimal('18.35') | false   | new BigDecimal('220.00') | new BigDecimal('201.65') | null                                                                                     | null                           | null
        "Voucher Not Found"     | "INVALID_VOUCHER" | new BigDecimal('18.35') | false   | new BigDecimal('220.00') | new BigDecimal('220.00') | new VoucherErrorResponse(VoucherStatus.NOT_FOUND.status, VoucherStatus.NOT_FOUND.status) | VoucherStatus.NOT_FOUND.status | VoucherStatus.NOT_FOUND.status
        "Failed Voucher"        | "ERROR_VOUCHER"   | new BigDecimal('18.35') | false   | new BigDecimal('220.00') | new BigDecimal('220.00') | new VoucherErrorResponse(VoucherStatus.FAILED.status, VoucherStatus.FAILED.status)       | VoucherStatus.FAILED.status    | VoucherStatus.FAILED.status
    }

    def 'Text Compute Parcel Cost - Apply Expired Voucher'() {
        given:
        def voucherCode = 'EXPIRED_VOUCHER'
        def discount = new BigDecimal('18.35')
        def expired = true

        def parcelDTO = new ParcelDTO(
                new BigDecimal('11'),
                new BigDecimal('15.80'),
                new BigDecimal('3.67'),
                new BigDecimal('21'),
                voucherCode
        )

        voucherAdapter.getVoucherDiscount(voucherCode) >>
                VoucherResponse.builder()
                        .voucherCode(voucherCode)
                        .discount(discount)
                        .isExpired(expired)
                        .voucherErrorResponse(null)
                        .build()

        mockGetAllActiveParcelConditionOrderedByPriority()

        when:
        def response = parcelService.calculateCost(parcelDTO)

        then:
        !response.reject
        new BigDecimal('220.00') == response.grossCost
        new BigDecimal('220.00') == response.netCost

        def voucherResponse = response.getVoucherResponse()
        voucherCode == voucherResponse.getVoucherCode()
        discount == voucherResponse.getDiscount()
        expired == voucherResponse.expired

        VoucherStatus.EXPIRED.status == voucherResponse.voucherErrorResponse.status
        VoucherStatus.EXPIRED.message == voucherResponse.voucherErrorResponse.errorMessage
    }

    def 'Text Compute Parcel Cost - Voucher Not Found'() {
        given:
        def voucherCode = 'FAILED_VOUCHER'
        def discount = new BigDecimal('18.35')
        def expired = false

        def parcelDTO = new ParcelDTO(
                new BigDecimal('11'),
                new BigDecimal('15.80'),
                new BigDecimal('3.67'),
                new BigDecimal('21'),
                voucherCode
        )

        voucherAdapter.getVoucherDiscount(voucherCode) >> {throw new VoucherNotFoundException("Not Found")}
        mockGetAllActiveParcelConditionOrderedByPriority()

        when:
        def response = parcelService.calculateCost(parcelDTO)

        then:
        !response.reject
        new BigDecimal('220.00') == response.grossCost
        new BigDecimal('220.00') == response.netCost

        def voucherResponse = response.getVoucherResponse()
        voucherCode == voucherResponse.getVoucherCode()
        null == voucherResponse.getDiscount()
        expired == voucherResponse.expired

        VoucherStatus.NOT_FOUND.status == voucherResponse.voucherErrorResponse.status
        VoucherStatus.NOT_FOUND.message == voucherResponse.voucherErrorResponse.errorMessage
    }

    def 'Text Compute Parcel Cost - Unexpected error upon retrieving voucher'() {
        given:
        def voucherCode = 'FAILED_VOUCHER'
        def discount = new BigDecimal('18.35')
        def expired = false

        def parcelDTO = new ParcelDTO(
                new BigDecimal('11'),
                new BigDecimal('15.80'),
                new BigDecimal('3.67'),
                new BigDecimal('21'),
                voucherCode
        )

        voucherAdapter.getVoucherDiscount(voucherCode) >> {throw new Exception()}
        mockGetAllActiveParcelConditionOrderedByPriority()

        when:
        def response = parcelService.calculateCost(parcelDTO)

        then:
        !response.reject
        new BigDecimal('220.00') == response.grossCost
        new BigDecimal('220.00') == response.netCost

        def voucherResponse = response.getVoucherResponse()
        voucherCode == voucherResponse.getVoucherCode()
        null == voucherResponse.getDiscount()
        expired == voucherResponse.expired

        VoucherStatus.FAILED.status == voucherResponse.voucherErrorResponse.status
        VoucherStatus.FAILED.message == voucherResponse.voucherErrorResponse.errorMessage
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
