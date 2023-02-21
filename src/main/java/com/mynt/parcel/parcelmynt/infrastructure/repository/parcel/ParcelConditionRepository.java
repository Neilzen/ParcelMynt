package com.mynt.parcel.parcelmynt.infrastructure.repository.parcel;

import com.mynt.parcel.parcelmynt.core.adapter.ParcelConditionAdapter;
import com.mynt.parcel.parcelmynt.core.entity.ParcelConditionEntity;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;

@Component(value = "parcelConditionAdapter")
public class ParcelConditionRepository implements ParcelConditionAdapter {

    private final List<ParcelConditionEntity> entityList;

    public ParcelConditionRepository() {
        entityList = Arrays.asList(
        new ParcelConditionEntity(
                0,
                0,
                "Reject",
                "weight > limit",
                new BigDecimal(50),
                BigDecimal.ZERO,
                "",
                true,
                true),
                new ParcelConditionEntity(
                        0,
                        1,
                        "Heavy Parcel",
                        "weight > limit",
                        new BigDecimal(10),
                        new BigDecimal("20"),
                        "cost * weight",
                        false,
                        true),
                new ParcelConditionEntity(
                        2,
                        3,
                        "Small Parcel",
                        "volume < limit",
                        new BigDecimal(1500),
                        new BigDecimal("0.03"),
                        "cost * volume",
                        false,
                        true),
                new ParcelConditionEntity(
                        3,
                        4,
                        "Medium Parcel",
                        "volume < limit",
                        new BigDecimal(2500),
                        new BigDecimal("0.04"),
                        "cost * volume",
                        false,
                        true),
                new ParcelConditionEntity(
                        4,
                        5,
                        "Large Parcel",
                        "",
                        null,
                        new BigDecimal("0.05"),
                        "cost * volume",
                        false,
                        true)
        );
    }


    @Override
    public List<ParcelConditionEntity> getAllActiveParcelConditionOrderedByPriority() {
        return entityList;
    }
}
