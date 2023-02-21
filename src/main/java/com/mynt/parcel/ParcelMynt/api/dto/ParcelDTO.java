package com.mynt.parcel.ParcelMynt.api.dto;

import java.math.BigDecimal;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParcelDTO {

    @NotNull(message = "Weight cannot be empty")
    @DecimalMin(value = "0.0", inclusive = false, message = "Weight invalid value")
    @Digits(integer=15, fraction=2, message = "Weight invalid value")
    private BigDecimal weight;

    @NotNull(message = "Height cannot be empty")
    @DecimalMin(value = "0.0", inclusive = false, message = "Height invalid value")
    @Digits(integer=15, fraction=2, message = "Height invalid value")
    private BigDecimal height;

    @NotNull(message = "Width cannot be empty")
    @DecimalMin(value = "0.0", inclusive = false, message = "Width invalid value")
    @Digits(integer=15, fraction=2, message = "Width invalid value")
    private BigDecimal width;

    @NotNull(message = "Length cannot be empty")
    @DecimalMin(value = "0.0", inclusive = false, message = "Length invalid value")
    @Digits(integer=15, fraction=2, message = "Length invalid value")
    private BigDecimal length;

    @Pattern(regexp = "^[a-zA-Z0-9_]*$", message = "Voucher code format is invalid.")
    private String voucherCode;

}
