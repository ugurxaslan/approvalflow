package com.ugurxaslan.approvalflow.mapper;

import com.ugurxaslan.approvalflow.model.dto.request.RequestDTO;
import com.ugurxaslan.approvalflow.model.dto.response.RequestResponseDTO;
import com.ugurxaslan.approvalflow.model.entity.Request;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = {RequestDetailMapper.class, ApprovalStepMapper.class})
public interface RequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "requestedBy", ignore = true)
    @Mapping(target = "approvalSteps", ignore = true)
    Request toEntity(RequestDTO dto);

    @Mapping(source = "requestedBy.id", target = "requestedById")
    @Mapping(source = "detail", target = "detail")
    @Mapping(source = "approvalSteps", target = "approvalSteps")
    RequestResponseDTO toResponseDto(Request entity);

}
