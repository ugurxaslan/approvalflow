package com.ugurxaslan.approvalflow.mapper;

import com.ugurxaslan.approvalflow.model.dto.shared.RequestDetailDTO;
import com.ugurxaslan.approvalflow.model.dto.shared.LeaveRequestDetailDTO;
import com.ugurxaslan.approvalflow.model.dto.shared.SalaryAdvanceRequestDetailDTO;
import com.ugurxaslan.approvalflow.model.entity.RequestDetail;
import com.ugurxaslan.approvalflow.model.entity.requestDetails.LeaveRequestDetail;
import com.ugurxaslan.approvalflow.model.entity.requestDetails.SalaryAdvanceRequestDetail;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
public interface RequestDetailMapper {

    @SubclassMappings({
            @SubclassMapping(source= LeaveRequestDetail.class ,target = LeaveRequestDetailDTO.class),
            @SubclassMapping(source= SalaryAdvanceRequestDetail.class ,target = SalaryAdvanceRequestDetailDTO.class)
            //add new subclass soruce and target
    })
    RequestDetailDTO toDto(RequestDetail entity);

    @SubclassMappings({
            @SubclassMapping(source= LeaveRequestDetailDTO.class ,target = LeaveRequestDetail.class),
            @SubclassMapping(source= SalaryAdvanceRequestDetailDTO.class ,target = SalaryAdvanceRequestDetail.class)
            //add new subclassMapping soruce and target
    })
    @Mapping(target = "request", ignore = true)
    RequestDetail toEntity(RequestDetailDTO dto);

    LeaveRequestDetailDTO toDto(LeaveRequestDetail entity);
    @Mapping(target = "request", ignore = true)
    LeaveRequestDetail toEntity(LeaveRequestDetailDTO dto);

    SalaryAdvanceRequestDetailDTO toDto(SalaryAdvanceRequestDetail entity);
    @Mapping(target = "request", ignore = true)
    SalaryAdvanceRequestDetail toEntity(SalaryAdvanceRequestDetailDTO dto);

    //add new subclass toDto and toEntity method signature
}
