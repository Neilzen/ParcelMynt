package com.mynt.parcel.parcelmynt.infrastructure.configuration;

import com.mynt.parcel.parcelmynt.core.exception.VoucherException;
import com.mynt.parcel.parcelmynt.core.exception.VoucherNotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class VoucherClientConfiguration implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus responseStatus = HttpStatus.valueOf(response.status());

        if (responseStatus.is4xxClientError()) {
            return new VoucherNotFoundException("Voucher not found");
        } else {
            return new VoucherException("Unexpected Exception");
        }
    }
}
