package com.ugurxaslan.approvalflow.model.dto.response;

import com.ugurxaslan.approvalflow.model.dto.shared.RequestDetailDTO;
import com.ugurxaslan.approvalflow.model.enums.RequestStatus;
import com.ugurxaslan.approvalflow.model.enums.RequestType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestResponseDTO {

    private Long Id;
    private RequestType requestType;
    private RequestStatus status;
    private String title;
    private String description;

    private Long requestedById;
    private List<ApprovalStepResponseDTO> approvalSteps;
    private RequestDetailDTO detail;

}
