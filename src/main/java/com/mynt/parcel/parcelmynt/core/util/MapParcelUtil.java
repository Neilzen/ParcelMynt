package com.mynt.parcel.parcelmynt.core.util;

import com.mynt.parcel.parcelmynt.api.dto.ParcelDTO;
import com.mynt.parcel.parcelmynt.core.model.Parcel;

public class MapParcelUtil {

    public static Parcel dtoToModel(ParcelDTO parcelDTO) {
        return Parcel.builder()
                .weight(parcelDTO.getWeight())
                .length(parcelDTO.getLength())
                .height(parcelDTO.getHeight())
                .width(parcelDTO.getWidth())
                .build();
    }

}
