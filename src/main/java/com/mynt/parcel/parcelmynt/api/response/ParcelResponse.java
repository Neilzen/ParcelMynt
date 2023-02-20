package com.mynt.parcel.parcelmynt.api.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParcelResponse {

    private BigDecimal grossCost;
    private BigDecimal netCost;
    private boolean reject;
    private VoucherResponse voucherResponse;

}
