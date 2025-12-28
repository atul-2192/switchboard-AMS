package com.SwitchBoard.AuthService.Model;


import com.SwitchBoard.AuthService.DTO.Account.USER_ROLE;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "account",
        indexes = {
                @Index(name = "idx_account_email", columnList = "email", unique = true)
        }
)
public class Account {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String mobile;
    private String linkedinUrl;
    private String githubUrl;
    private String leetcodeUrl;
    private String cvPath;

    private Date deadline;
    private String aimRole;
    @Column(name = "current_role_name")
    private String currentRole;

    private int totalRewardPoints=0;
    private int taskAssignedCount=0;
    private int taskCompletedCount=0;

    // GOOGLE LOGIN FIELDS
    private String googleId;              // Google Unique ID (sub)
    private String profileImageUrl;       // Google Profile Photo
    private boolean googleAccount;        // true if logged in via Google

    @Enumerated(EnumType.STRING)
    private List<USER_ROLE> userRole = Collections.singletonList(USER_ROLE.USER);

    @CreationTimestamp
    private Date createdAt ;

    @UpdateTimestamp
    private Date updatedAt;

}

