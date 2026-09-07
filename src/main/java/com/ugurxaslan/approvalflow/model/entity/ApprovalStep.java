package com.ugurxaslan.approvalflow.model.entity;

import com.ugurxaslan.approvalflow.model.enums.DepartmentType;
import com.ugurxaslan.approvalflow.model.enums.StepStatus;
import com.ugurxaslan.approvalflow.model.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "approval_steps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalStep extends BaseEntity {

    @Column(name = "step_order", nullable = false)
    private Integer stepOrder;

    @Column(name = "comment")
    private String comment;

    @Column(name = "action_date")
    private LocalDateTime actionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StepStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "required_role", nullable = false)
    private UserRole requiredRole;
    //relations

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "request_id", nullable = false)
    private Request request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_approver")
    private User assignedApprover;

}