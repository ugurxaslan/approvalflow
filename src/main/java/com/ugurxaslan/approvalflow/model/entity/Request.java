package com.ugurxaslan.approvalflow.model.entity;

import com.ugurxaslan.approvalflow.model.enums.RequestStatus;
import com.ugurxaslan.approvalflow.model.enums.RequestType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Request extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type", nullable = false)
    private RequestType requestType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RequestStatus status;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", length = 1000)
    private String description;

    //relations

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requested_by_id", nullable = false)
    private User requested_by;

    @OneToMany(
            mappedBy = "request",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @OrderBy("stepOrder ASC")
    @Builder.Default
    private List<ApprovalStep> approvalSteps = new ArrayList<>();

}