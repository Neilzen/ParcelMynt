package com.mynt.parcel.parcelmynt.infrastructure.voucher;

import com.mynt.parcel.parcelmynt.infrastructure.configuration.VoucherClientConfiguration;
import com.mynt.parcel.parcelmynt.infrastructure.voucher.Response.VoucherClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        value = "voucherClient",
        url = "${outbound.voucher.url}",
        configuration = VoucherClientConfiguration.class
)
public interface VoucherClient {

    @GetMapping("/voucher/{voucherCode}")
    VoucherClientResponse getVoucher(@PathVariable String voucherCode,
                                                     @RequestParam String key);
}
