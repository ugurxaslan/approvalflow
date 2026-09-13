package com.ugurxaslan.approvalflow.model.dto.shared;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.ugurxaslan.approvalflow.model.enums.RequestType;
import lombok.Getter;
import lombok.Setter;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
        property = "requestType",
        defaultImpl = Void.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LeaveRequestDetailDTO.class, name = RequestType.Types.LEAVE),
        @JsonSubTypes.Type(value = SalaryAdvanceRequestDetailDTO.class, name = RequestType.Types.SALARY_ADVANCE),
})
@Getter
@Setter
public abstract class RequestDetailDTO {}