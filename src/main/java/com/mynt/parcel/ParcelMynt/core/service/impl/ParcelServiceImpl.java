package com.mynt.parcel.ParcelMynt.core.service.impl;

import com.mynt.parcel.ParcelMynt.adapter.VoucherAdapter;
import com.mynt.parcel.ParcelMynt.api.dto.ParcelDTO;
import com.mynt.parcel.ParcelMynt.api.response.VoucherResponse;
import com.mynt.parcel.ParcelMynt.core.service.ParcelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ParcelServiceImpl implements ParcelService {

    private final VoucherAdapter voucherAdapter;

    @Autowired
    public ParcelServiceImpl(VoucherAdapter voucherAdapter) {
        this.voucherAdapter = voucherAdapter;
    }

    @Override
    public VoucherResponse calculateCost(ParcelDTO parcelDTO) {
        return null;
    }
}
