package com.mynt.parcel.parcelmynt.api;

import com.mynt.parcel.parcelmynt.api.dto.ParcelDTO;
import com.mynt.parcel.parcelmynt.api.response.ParcelResponse;
import com.mynt.parcel.parcelmynt.api.response.VoucherErrorResponse;
import com.mynt.parcel.parcelmynt.api.response.VoucherResponse;
import com.mynt.parcel.parcelmynt.core.service.ParcelService;
import com.mynt.parcel.parcelmynt.core.service.impl.ParcelServiceImpl;
import java.math.BigDecimal;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/delivery")
public class DeliveryApi {

    private final ParcelService parcelService;

    @Autowired
    public DeliveryApi(ParcelService parcelService) {
        this.parcelService = parcelService;
    }

    @PostMapping("/parcel")
    public ResponseEntity<ParcelResponse> computeParcelCost(@RequestBody @Valid ParcelDTO parcelDTO) {
        log.info("Start - Compute Parcel Cost");
        ParcelResponse parcelResponse = parcelService.calculateCost(parcelDTO);
        log.info("End - Compute Parcel Cost");
        return ResponseEntity.ok(parcelResponse);
    }

}
