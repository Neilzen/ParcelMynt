package com.mynt.parcel.parcelmynt.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoucherErrorResponse {

    private String status;
    private String errorMessage;

}
