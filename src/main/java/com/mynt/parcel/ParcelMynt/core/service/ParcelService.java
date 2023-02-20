package com.mynt.parcel.ParcelMynt.core.service;

import com.mynt.parcel.ParcelMynt.api.dto.ParcelDTO;
import com.mynt.parcel.ParcelMynt.api.response.VoucherResponse;

public interface ParcelService {

    VoucherResponse calculateCost(ParcelDTO parcelDTO);

}
