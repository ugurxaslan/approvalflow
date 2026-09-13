package com.ugurxaslan.approvalflow.mapper;


import com.ugurxaslan.approvalflow.model.dto.response.ApprovalStepResponseDTO;
import com.ugurxaslan.approvalflow.model.entity.ApprovalStep;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel="spring")
public interface ApprovalStepMapper {

    @Mapping(source = "assignedApprover.id",target = "assignedApproverId")
    ApprovalStepResponseDTO toDto (ApprovalStep entity);

    List<ApprovalStepResponseDTO> toDtoList(List<ApprovalStep> entities);
}
