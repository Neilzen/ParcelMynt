package com.mynt.parcel.parcelmynt.core.exception;

public class VoucherException extends RuntimeException {

    public VoucherException(String message) {
        super(message);
    }

    public VoucherException(String message, Throwable e) {
        super(message, e);
    }

}
