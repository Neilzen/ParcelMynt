package com.mynt.parcel.parcelmynt.core.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum VoucherStatus {
    EXPIRED("Expired", "Voucher applied is expired"),
    NOT_FOUND("Not found", "Voucher applied does not exist"),
    FAILED("Failed", "Error applying voucher");

    private final String status;
    private final String message;
}
