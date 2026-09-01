package com.ugurxaslan.approvalflow.model.entity;

import com.ugurxaslan.approvalflow.model.enums.DepartmentType;
import com.ugurxaslan.approvalflow.model.enums.StepStatus;
import com.ugurxaslan.approvalflow.model.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "approval_step_target_roles",
            joinColumns = @JoinColumn(name = "approval_step_id")
    )
    @Column(name = "target_role", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<UserRole> targetRoles = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "target_department",nullable = false)
    private DepartmentType targetDepartment;

    //relations

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "request_id", nullable = false)
    private Request request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver")
    private User approver;

}