package com.mynt.parcel.parcelmynt.core.service.impl;

import com.mynt.parcel.parcelmynt.core.adapter.ParcelConditionAdapter;
import com.mynt.parcel.parcelmynt.core.adapter.VoucherAdapter;
import com.mynt.parcel.parcelmynt.api.dto.ParcelDTO;
import com.mynt.parcel.parcelmynt.api.response.ParcelResponse;
import com.mynt.parcel.parcelmynt.api.response.VoucherErrorResponse;
import com.mynt.parcel.parcelmynt.api.response.VoucherResponse;
import com.mynt.parcel.parcelmynt.core.entity.ParcelConditionEntity;
import com.mynt.parcel.parcelmynt.core.enumeration.VoucherStatus;
import com.mynt.parcel.parcelmynt.core.exception.ParcelException;
import com.mynt.parcel.parcelmynt.core.exception.VoucherNotFoundException;
import com.mynt.parcel.parcelmynt.core.model.Parcel;
import com.mynt.parcel.parcelmynt.core.service.ParcelService;
import com.mynt.parcel.parcelmynt.core.util.ExpressionUtil;
import com.mynt.parcel.parcelmynt.core.util.MapParcelUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

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

        log.info("Parcel Dimension. Weight: {}kg, Height: {}cm, Width: {}cm, Length: {}cm, Volume: {}c㎥",
                parcel.getWeight(),
                parcel.getHeight(),
                parcel.getWidth(),
                parcel.getLength(),
                parcel.getVolume()
        );

        ParcelConditionEntity parcelConditionEntity = getApplicationConditionElseThrowException(parcel);

        if (parcelConditionEntity.isReject()) {
            log.info("Parcel Rejected");
            return ParcelResponse.builder()
                    .grossCost(new BigDecimal("0.00"))
                    .netCost(new BigDecimal("0.00"))
                    .reject(true)
                    .build();
        }

        BigDecimal grossCost = ExpressionUtil.executeCostExpression(
                parcel,
                parcelConditionEntity.getCost(),
                parcelConditionEntity.getCostExpression()
        );

        log.info("Calculated Gross Cost of Php{}", grossCost.toPlainString());

        Optional<VoucherResponse> optionalVoucherResponse = getVoucherDiscount(parcelDTO.getVoucherCode());

        if (optionalVoucherResponse.isPresent()) {
            return applyVoucher(optionalVoucherResponse.get(), grossCost);
        }

        log.info("End - Calculate Cost - No Voucher Applied");
        return ParcelResponse.builder()
                .grossCost(grossCost)
                .netCost(grossCost)
                .voucherResponse(VoucherResponse.builder()
                        .voucherCode("")
                        .discount(new BigDecimal("0.00"))
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

        if (optionalParcelConditionEntity.isEmpty()) {
            throw new ParcelException("No Valid condition found");
        }

        return optionalParcelConditionEntity.get();
    }

    private Optional<VoucherResponse> getVoucherDiscount(String voucherCode) {
        if (!StringUtils.hasLength(voucherCode)) {
            log.info("No voucher code applied");
            return Optional.empty();
        }

        try {
            log.info("Retrieving voucher code: {}", voucherCode);
            VoucherResponse voucherResponse = voucherAdapter.getVoucherDiscount(voucherCode);
            log.info("Retrieved voucher: {}", voucherResponse);
            return Optional.of(voucherResponse);
        } catch (VoucherNotFoundException e) {
            log.error("Voucher not found occured upon fetching voucher discount.", e);
            VoucherErrorResponse voucherErrorResponse = VoucherErrorResponse.builder()
                    .status(VoucherStatus.NOT_FOUND.getStatus())
                    .errorMessage(VoucherStatus.NOT_FOUND.getMessage())
                    .build();

            return Optional.of(VoucherResponse.builder()
                    .voucherCode(voucherCode)
                    .voucherErrorResponse(voucherErrorResponse)
                    .build());
        } catch (Exception e) {
            log.error("Unexpected error occurred upon fetching voucher discount.", e);
            VoucherErrorResponse voucherErrorResponse = VoucherErrorResponse.builder()
                    .status(VoucherStatus.FAILED.getStatus())
                    .errorMessage(VoucherStatus.FAILED.getMessage())
                    .build();

            return Optional.of(VoucherResponse.builder()
                    .voucherCode(voucherCode)
                    .voucherErrorResponse(voucherErrorResponse)
                    .build());
        }
    }

    private ParcelResponse applyVoucher(VoucherResponse voucherResponse, BigDecimal grossCost) {
        if (ObjectUtils.isEmpty(voucherResponse.getVoucherErrorResponse())) {
            if (isVoucherExpired(voucherResponse)) {
                return ParcelResponse.builder()
                        .grossCost(grossCost)
                        .netCost(grossCost)
                        .voucherResponse(voucherResponse)
                        .build();
            }

            return applyVoucherAndBuildResponse(voucherResponse, grossCost);

        } else {
            log.info("Voucher {} was not applied as error was encountered", voucherResponse.getVoucherCode());
            return ParcelResponse.builder()
                    .grossCost(grossCost)
                    .netCost(grossCost)
                    .voucherResponse(voucherResponse)
                    .build();
        }
    }

    private static ParcelResponse applyVoucherAndBuildResponse(VoucherResponse voucherResponse, BigDecimal grossCost) {
        BigDecimal discount = voucherResponse.getDiscount();

        log.info("Applying discount Php {} from discount code {}", discount.toPlainString(), voucherResponse.getVoucherCode());

        return ParcelResponse.builder()
                .grossCost(grossCost)
                .netCost(grossCost.subtract(discount).setScale(2, RoundingMode.HALF_UP))
                .voucherResponse(voucherResponse)
                .build();
    }

    private static boolean isVoucherExpired(VoucherResponse voucherResponse) {
        if (voucherResponse.isExpired()) {
            log.info("Voucher {} was not applied as its expired", voucherResponse.getVoucherCode());
            VoucherErrorResponse voucherErrorResponse = VoucherErrorResponse.builder()
                    .status(VoucherStatus.EXPIRED.getStatus())
                    .errorMessage(VoucherStatus.EXPIRED.getMessage())
                    .build();

            voucherResponse.setVoucherErrorResponse(voucherErrorResponse);

            return true;
        }
        return false;
    }


}
