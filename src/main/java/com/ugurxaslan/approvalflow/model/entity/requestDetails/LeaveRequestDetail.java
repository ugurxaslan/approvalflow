package com.ugurxaslan.approvalflow.model.entity.requestDetails;

import com.ugurxaslan.approvalflow.model.entity.RequestDetail;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "leave_request_details")
@Getter
@Setter
public class LeaveRequestDetail extends RequestDetail {

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

}