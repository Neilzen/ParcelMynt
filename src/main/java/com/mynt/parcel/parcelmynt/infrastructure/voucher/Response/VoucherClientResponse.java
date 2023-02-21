package com.mynt.parcel.parcelmynt.infrastructure.voucher.Response;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoucherClientResponse {

    private String code;
    private float discount;
    private LocalDate expiry;

}
