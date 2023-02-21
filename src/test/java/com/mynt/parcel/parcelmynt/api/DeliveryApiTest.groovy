package com.mynt.parcel.parcelmynt.api

import com.mynt.parcel.parcelmynt.api.dto.ParcelDTO
import com.mynt.parcel.parcelmynt.api.response.ParcelResponse
import com.mynt.parcel.parcelmynt.core.adapter.ParcelConditionAdapter
import com.mynt.parcel.parcelmynt.core.entity.ParcelConditionEntity
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

import java.util.stream.Collectors

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(properties = ['spring.main.allow-bean-definition-overriding=true'],
        webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebClient
class DeliveryApiTest extends Specification {

    private static final String urlBase = "/api/delivery"
    private static final String urlParcel = urlBase + "/parcel"

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    ParcelConditionAdapter parcelConditionAdapter;

    void 'Parcel Post - where all request are valid'() {
        given:
        def requestBody = new ParcelDTO(weight: new BigDecimal("25"),
                height: new BigDecimal("10.5"),
                length: new BigDecimal("14.35"),
                width: new BigDecimal("8.00"),
                voucherCode: "VOUCHER_111")

        def request = new HttpEntity(requestBody)

        mockGetAllActiveParcelConditionOrderedByPriority()

        when:
        def response = restTemplate.postForEntity(urlParcel,
                requestBody,
                ParcelResponse)

        then:
        HttpStatus.OK == response.statusCode
    }


    @Unroll
    void 'Parcel Post - where #scope'() {
        given:
        def requestBody = new ParcelDTO(weight: weight,
                height: height,
                length: length,
                width: width,
                voucherCode: voucherCode)

        def request = new HttpEntity(requestBody)

        when:
        def response = restTemplate.postForEntity(urlParcel,
                requestBody,
                LinkedHashMap)

        then:
        HttpStatus.BAD_REQUEST == response.statusCode
        errorResponse.stream()
                .filter(it -> !((List<String>) response.body.get("errors")).contains(it))
                .collect(Collectors.toList())
                .isEmpty()


        where:
        scope                 | weight                                  | height                                  | length                                  | width                                   | voucherCode | errorResponse
        'Weight is empty'     | null                                    | new BigDecimal('10.5')                  | new BigDecimal('14.35')                 | new BigDecimal(8.00)                    | null        | ["Weight cannot be empty"]
        'Weight is invalid'   | new BigDecimal('10.5123')               | new BigDecimal('10.5')                  | new BigDecimal('14.35')                 | new BigDecimal(8.00)                    | null        | ["Weight invalid value"]
        'Weight is invalid'   | new BigDecimal('123456789012345678.51') | new BigDecimal('10.5')                  | new BigDecimal('14.35')                 | new BigDecimal(8.00)                    | null        | ["Weight invalid value"]
        'Weight is invalid'   | new BigDecimal('-1')                    | new BigDecimal('10.5')                  | new BigDecimal('14.35')                 | new BigDecimal(8.00)                    | null        | ["Weight invalid value"]
        'Height is empty'     | new BigDecimal("25")                    | null                                    | new BigDecimal('14.35')                 | new BigDecimal(8.00)                    | null        | ["Height cannot be empty"]
        'Height is invalid'   | new BigDecimal("25")                    | new BigDecimal('10.5123')               | new BigDecimal('14.35')                 | new BigDecimal(8.00)                    | null        | ["Height invalid value"]
        'Height is invalid'   | new BigDecimal("25")                    | new BigDecimal('123456789012345678.51') | new BigDecimal('14.35')                 | new BigDecimal(8.00)                    | null        | ["Height invalid value"]
        'Height is invalid'   | new BigDecimal("25")                    | new BigDecimal('-1')                    | new BigDecimal('14.35')                 | new BigDecimal(8.00)                    | null        | ["Height invalid value"]
        'Length is empty'     | new BigDecimal("25")                    | new BigDecimal('10.5')                  | null                                    | new BigDecimal(8.00)                    | null        | ["Length cannot be empty"]
        'Length is invalid'   | new BigDecimal("25")                    | new BigDecimal('10.5')                  | new BigDecimal('123456789012345678.51') | new BigDecimal(8.00)                    | null        | ["Length invalid value"]
        'Length is invalid'   | new BigDecimal("25")                    | new BigDecimal('10.5')                  | new BigDecimal('-1')                    | new BigDecimal(8.00)                    | null        | ["Length invalid value"]
        'Length is invalid'   | new BigDecimal("25")                    | new BigDecimal('10.5')                  | new BigDecimal('10.5123')               | new BigDecimal(8.00)                    | null        | ["Length invalid value"]
        'Width is empty'      | new BigDecimal("25")                    | new BigDecimal('10.5')                  | new BigDecimal('14.35')                 | null                                    | null        | ["Width cannot be empty"]
        'Width is invalid'    | new BigDecimal("25")                    | new BigDecimal('10.5')                  | new BigDecimal('14.35')                 | new BigDecimal('10.5123')               | null        | ["Width invalid value"]
        'Width is invalid'    | new BigDecimal("25")                    | new BigDecimal('10.5')                  | new BigDecimal('14.35')                 | new BigDecimal('123456789012345678.51') | null        | ["Width invalid value"]
        'Width is invalid'    | new BigDecimal("25")                    | new BigDecimal('10.5')                  | new BigDecimal('14.35')                 | new BigDecimal('-1')                    | null        | ["Width invalid value"]
        'Voucher invalid'     | new BigDecimal("25")                    | new BigDecimal('10.5')                  | new BigDecimal('14.35')                 | new BigDecimal(8.00)                    | "abc_12>>"  | ["Voucher code format is invalid."]
        'More than one issue' | new BigDecimal("25.1234")               | new BigDecimal('1234567890123456.5')    | new BigDecimal('-1')                    | null                                    | "abc_12>>"  | ["Weight invalid value", "Height invalid value", "Length invalid value", "Width cannot be empty", "Voucher code format is invalid."]
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
