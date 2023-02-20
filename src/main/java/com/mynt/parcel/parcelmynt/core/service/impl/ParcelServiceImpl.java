package com.mynt.parcel.parcelmynt.core.service.impl;

import com.mynt.parcel.parcelmynt.adapter.ParcelConditionAdapter;
import com.mynt.parcel.parcelmynt.adapter.VoucherAdapter;
import com.mynt.parcel.parcelmynt.api.dto.ParcelDTO;
import com.mynt.parcel.parcelmynt.api.response.ParcelResponse;
import com.mynt.parcel.parcelmynt.api.response.VoucherResponse;
import com.mynt.parcel.parcelmynt.core.entity.ParcelConditionEntity;
import com.mynt.parcel.parcelmynt.core.exception.ParcelException;
import com.mynt.parcel.parcelmynt.core.model.Parcel;
import com.mynt.parcel.parcelmynt.core.service.ParcelService;
import com.mynt.parcel.parcelmynt.core.util.ExpressionUtil;
import com.mynt.parcel.parcelmynt.core.util.MapParcelUtil;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ParcelServiceImpl implements ParcelService {

    private final VoucherAdapter voucherAdapter;
    private final ParcelConditionAdapter parcelConditionAdapter;

    @Autowired
    public ParcelServiceImpl(VoucherAdapter voucherAdapter, ParcelConditionAdapter parcelConditionAdapter) {
        this.voucherAdapter = voucherAdapter;
        this.parcelConditionAdapter = parcelConditionAdapter;
    }

    @Override
    public ParcelResponse calculateCost(ParcelDTO parcelDTO) {
        log.info("Start - Calculate Cost");
        Parcel parcel = MapParcelUtil.dtoToModel(parcelDTO);

        ParcelConditionEntity parcelConditionEntity = getApplicationConditionElseThrowException(parcel);

        if(parcelConditionEntity.isReject()) {
            return ParcelResponse.builder()
                    .grossCost(new BigDecimal("0.00"))
                    .netCost(new BigDecimal("0.00"))
                    .reject(true)
                    .build();
        }

        BigDecimal cost = ExpressionUtil.executeCostExpression(
                parcel,
                parcelConditionEntity.getCost(),
                parcelConditionEntity.getCostExpression()
        );

        log.info("End - Calculate Cost");
        return ParcelResponse.builder()
                .grossCost(cost)
                .netCost(cost)
                .voucherResponse(VoucherResponse.builder()
                        .voucherCode("")
                        .discount(BigDecimal.ZERO)
                        .isExpired(false)
                        .build())
                .build();
    }

    private ParcelConditionEntity getApplicationConditionElseThrowException(Parcel parcel) {
        List<ParcelConditionEntity> parcelConditionEntityList = parcelConditionAdapter.getAllActiveParcelConditionOrderedByPriority();

        Optional<ParcelConditionEntity> optionalParcelConditionEntity = parcelConditionEntityList.stream().filter(conditionEntity ->
                ExpressionUtil.executeConditionExpression(
                        conditionEntity.getConditionExpression(),
                        parcel,
                        conditionEntity.getConditionLimit())
        ).findFirst();

        if(optionalParcelConditionEntity.isEmpty()) {
            throw new ParcelException("No Valid condition found");
        }

        return optionalParcelConditionEntity.get();
    }

    private VoucherResponse getVoucherDiscount(String voucherCode) {

        return new VoucherResponse();
    }


}
