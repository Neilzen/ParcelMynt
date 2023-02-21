package com.mynt.parcel.parcelmynt.core.exception;

public class ParcelException extends RuntimeException {

    public ParcelException(String message) {
        super(message);
    }

    public ParcelException(String message, Throwable e) {
        super(message, e);
    }

}
