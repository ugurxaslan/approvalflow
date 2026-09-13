package com.ugurxaslan.approvalflow.model.dto.shared;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SalaryAdvanceRequestDetailDTO extends RequestDetailDTO {

    @NotNull(message = "Advance amount is required")
    @Positive(message = "Advance amount must be greater than zero")
    private BigDecimal amount;
}
