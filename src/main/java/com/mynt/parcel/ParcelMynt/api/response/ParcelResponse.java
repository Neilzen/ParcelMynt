package com.mynt.parcel.ParcelMynt.api.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParcelResponse {

    private BigDecimal grossCost;
    private BigDecimal netCost;
    private VoucherResponse voucherResponse;

}
