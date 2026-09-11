package com.ugurxaslan.approvalflow.model.entity;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "request_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestDetail extends BaseEntity{

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "request_id")
    private Request request;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "json_payload", nullable = false)
    private String jsonPayload;
}
