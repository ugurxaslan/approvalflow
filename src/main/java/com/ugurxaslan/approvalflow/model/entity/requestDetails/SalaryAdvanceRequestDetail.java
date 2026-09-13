package com.ugurxaslan.approvalflow.model.entity.requestDetails;

import com.ugurxaslan.approvalflow.model.entity.RequestDetail;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "salary_advance_request_details")
@Getter
@Setter
public class SalaryAdvanceRequestDetail extends RequestDetail {

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;
}