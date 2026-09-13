package com.ugurxaslan.approvalflow.model.dto.request;

import com.ugurxaslan.approvalflow.model.dto.shared.BaseRequestDetailDTO;
import com.ugurxaslan.approvalflow.model.enums.RequestType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestDTO {

    @NotNull(message = "Request type is required")
    private RequestType requestType;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @Valid
    private BaseRequestDetailDTO detail;
}
