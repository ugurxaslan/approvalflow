package com.ugurxaslan.approvalflow.model.entity;


import com.ugurxaslan.approvalflow.model.enums.DepartmentType;
import com.ugurxaslan.approvalflow.model.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<UserRole> userRoles = new HashSet<>();

    //relations

    @Enumerated(EnumType.STRING)
    @Column(name = "department_type", nullable = false, length = 50)
    private DepartmentType departmentType;
}