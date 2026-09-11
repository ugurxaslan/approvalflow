package com.ugurxaslan.approvalflow.model.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.ugurxaslan.approvalflow.model.enums.RequestType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "requestType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LeaveRequestDTO.class, name = RequestType.Types.LEAVE),
        @JsonSubTypes.Type(value = SalaryAdvanceRequestDTO.class, name = RequestType.Types.SALARY_ADVANCE),
        @JsonSubTypes.Type(value = GenericRequestDTO.class, name = RequestType.Types.SOFTWARE_LICENSE),
        @JsonSubTypes.Type(value = GenericRequestDTO.class, name = RequestType.Types.TECHNICAL_SUPPORT)
})

@Getter
@Setter
public class BaseRequestDTO {

    @NotNull(message = "Request type is required")
    private RequestType requestType;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;
}
