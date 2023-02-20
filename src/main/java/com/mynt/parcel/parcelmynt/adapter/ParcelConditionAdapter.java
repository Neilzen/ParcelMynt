package com.mynt.parcel.parcelmynt.adapter;

import com.mynt.parcel.parcelmynt.core.entity.ParcelConditionEntity;
import java.util.List;

public interface ParcelConditionAdapter {

    List<ParcelConditionEntity> getAllActiveParcelConditionOrderedByPriority();

}
