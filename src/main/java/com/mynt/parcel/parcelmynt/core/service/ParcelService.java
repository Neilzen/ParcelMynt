package com.mynt.parcel.parcelmynt.core.service;

import com.mynt.parcel.parcelmynt.api.dto.ParcelDTO;
import com.mynt.parcel.parcelmynt.api.response.ParcelResponse;
import com.mynt.parcel.parcelmynt.api.response.VoucherResponse;

public interface ParcelService {

    ParcelResponse calculateCost(ParcelDTO parcelDTO);

}
