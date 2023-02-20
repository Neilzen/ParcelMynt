package com.mynt.parcel.parcelmynt.api

import com.mynt.parcel.parcelmynt.adapter.VoucherAdapter
import com.mynt.parcel.parcelmynt.core.service.ParcelService
import com.mynt.parcel.parcelmynt.core.service.impl.ParcelServiceImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import spock.mock.DetachedMockFactory

@Configuration
class ApiConfiguration {

    def mock = new DetachedMockFactory()

    @Bean
    ParcelService parcelService(VoucherAdapter voucherAdapter) {
        return new ParcelServiceImpl(voucherAdapter)
    }

    @Bean
    VoucherAdapter voucherAdapter() {
        return mock.Mock(VoucherAdapter)
    }

}
