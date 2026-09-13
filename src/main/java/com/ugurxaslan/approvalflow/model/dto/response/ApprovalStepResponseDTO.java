package com.ugurxaslan.approvalflow.model.dto.response;

import com.ugurxaslan.approvalflow.model.enums.StepStatus;
import com.ugurxaslan.approvalflow.model.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalStepResponseDTO {

    private Long id;
    private Integer stepOrder;
    private String comment;
    private LocalDateTime actionDate;
    private StepStatus status;
    private UserRole requiredRole;

    private Long assignedApproverId;
    //TODO:buraya username eklemeye çalışdiğer
    // katmanlarada müdahale etmen gerekebilir

}
