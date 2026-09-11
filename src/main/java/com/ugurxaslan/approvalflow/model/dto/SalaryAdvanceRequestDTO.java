package com.ugurxaslan.approvalflow.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SalaryAdvanceRequestDTO extends BaseRequestDTO{

    @NotNull(message = "Advance amount is required")
    @Positive(message = "Advance amount must be greater than zero")
    private BigDecimal requestedAmount;
}
