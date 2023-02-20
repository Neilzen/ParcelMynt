package com.mynt.parcel.parcelmynt.core.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Parcel {

    private BigDecimal weight;
    private BigDecimal height;
    private BigDecimal width;
    private BigDecimal length;

    public BigDecimal getVolume() {
        return height.multiply(width).multiply(length);
    }



}
