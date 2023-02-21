package com.mynt.parcel.parcelmynt.core.configuration

import com.mynt.parcel.parcelmynt.core.adapter.ParcelConditionAdapter
import com.mynt.parcel.parcelmynt.core.adapter.VoucherAdapter
import com.mynt.parcel.parcelmynt.core.service.ParcelService
import com.mynt.parcel.parcelmynt.core.service.impl.ParcelServiceImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import spock.mock.DetachedMockFactory

@Configuration
class CoreTestConfiguration {

    def mock = new DetachedMockFactory()

    @Bean
    ParcelService parcelService(VoucherAdapter voucherAdapter,
                                ParcelConditionAdapter parcelConditionAdapter) {
        return new ParcelServiceImpl(voucherAdapter, parcelConditionAdapter)
    }

    @Bean
    VoucherAdapter voucherAdapter() {
        return mock.Mock(VoucherAdapter)
    }

    @Bean
    ParcelConditionAdapter parcelConditionAdapter() {
        return mock.Mock(ParcelConditionAdapter)
    }

}
