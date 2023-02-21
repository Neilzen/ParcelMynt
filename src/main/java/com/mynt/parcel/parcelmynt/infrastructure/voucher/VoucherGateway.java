package com.mynt.parcel.parcelmynt.infrastructure.voucher;

import com.mynt.parcel.parcelmynt.api.response.VoucherResponse;
import com.mynt.parcel.parcelmynt.core.adapter.VoucherAdapter;
import com.mynt.parcel.parcelmynt.infrastructure.voucher.Response.VoucherClientResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component(value = "voucherAdapter")
@Slf4j
public class VoucherGateway implements VoucherAdapter {

    private final String apiKey;

    private final VoucherClient voucherClient;

    @Autowired
    public VoucherGateway(VoucherClient voucherClient,
                        @Value("${outbound.voucher.apikey}") String apiKey) {
        this.voucherClient = voucherClient;
        this.apiKey = apiKey;
    }

    @Override
    public VoucherResponse getVoucherDiscount(String voucherCode) {

        VoucherClientResponse response = voucherClient.getVoucher(voucherCode, apiKey);
        return VoucherResponse.builder()
                .voucherCode(voucherCode)
                .discount(BigDecimal.valueOf(response.getDiscount()))
                .isExpired(response.getExpiry().isAfter(LocalDate.now()))
                .build();
    }
}
