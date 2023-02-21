package com.mynt.parcel.parcelmynt.infrastructure.Configuration

import com.mynt.parcel.parcelmynt.core.adapter.ParcelConditionAdapter
import com.mynt.parcel.parcelmynt.infrastructure.voucher.VoucherClient
import com.mynt.parcel.parcelmynt.infrastructure.voucher.VoucherGateway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import spock.mock.DetachedMockFactory

@Configuration
class InfraTestConfiguration {

    def mock = new DetachedMockFactory()

    @Bean(name = "voucherAdapter")
    VoucherGateway voucherGateway() {
        return new VoucherGateway(voucherClient(), apiKey());
    }

    @Bean
    VoucherClient voucherClient() {
        return mock.Mock(VoucherClient)
    }

    @Bean
    String apiKey() {
        return "testKey"
    }

    @Bean(name = "parcelConditionAdapter")
    ParcelConditionAdapter parcelConditionAdapter() {
        return mock.Mock(ParcelConditionAdapter)
    }

}
