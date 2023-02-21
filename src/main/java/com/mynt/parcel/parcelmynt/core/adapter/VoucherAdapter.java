package com.mynt.parcel.parcelmynt.core.adapter;

import com.mynt.parcel.parcelmynt.api.response.VoucherResponse;
import java.math.BigDecimal;

public interface VoucherAdapter {

    VoucherResponse getVoucherDiscount(String voucherCode);

}
