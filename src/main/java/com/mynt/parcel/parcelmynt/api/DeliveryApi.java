package com.mynt.parcel.parcelmynt.api;

import com.mynt.parcel.parcelmynt.api.dto.ParcelDTO;
import com.mynt.parcel.parcelmynt.api.response.ParcelResponse;
import com.mynt.parcel.parcelmynt.api.response.VoucherResponse;
import java.math.BigDecimal;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/delivery")
public class DeliveryApi {

    @PostMapping("/parcel")
    public ResponseEntity<ParcelResponse> computeParcelCost(@RequestBody @Valid ParcelDTO parcelDTO) {
        ParcelResponse parcelResponse = new ParcelResponse(
                BigDecimal.valueOf(103.00),
                BigDecimal.valueOf(100.00),
                new VoucherResponse(
                        BigDecimal.valueOf(3.00),
                        "testVoucher",
                        false
                )
        );

        return ResponseEntity.ok(parcelResponse);
    }

}
