package com.mynt.parcel.parcelmynt.infrastructure.voucher

import com.mynt.parcel.parcelmynt.core.adapter.VoucherAdapter
import com.mynt.parcel.parcelmynt.core.configuration.CoreTestConfiguration
import com.mynt.parcel.parcelmynt.infrastructure.Configuration.InfraTestConfiguration
import com.mynt.parcel.parcelmynt.infrastructure.voucher.Response.VoucherClientResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import java.time.LocalDate

@ContextConfiguration(classes = [InfraTestConfiguration])
class VoucherGatewayTest extends Specification {

    @Autowired
    VoucherGateway voucherAdapter

    @Autowired
    VoucherClient voucherClient

    @Autowired
    String apiKey

    def 'Basic Test' () {
        given:
        def voucherCode = "TEST_VOUCHER"
        def discount = new BigDecimal('10.00')
        def expiryDate = LocalDate.now().minusDays(1l)

        voucherClient.getVoucher(voucherCode, apiKey) >> {
            return new VoucherClientResponse(
                    voucherCode,
                    new BigDecimal('10.00'),
                    expiryDate,
            )
        }

        when:
        def response = voucherAdapter.getVoucherDiscount(voucherCode)

        then:
        voucherCode == response.getVoucherCode()
        discount == response.getDiscount()
        !response.isExpired()
    }

}
