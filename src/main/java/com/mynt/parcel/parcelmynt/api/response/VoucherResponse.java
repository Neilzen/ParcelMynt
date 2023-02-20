package com.mynt.parcel.parcelmynt.api.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoucherResponse {

    private BigDecimal discount;
    private String voucherCode;
    private boolean isExpired;

}
