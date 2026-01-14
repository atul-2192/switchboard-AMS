package com.SwitchBoard.AuthService.DTO.Account;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountResponseDto {
    private UUID id;
    private String name;
    private String email;

    private String mobile;
    private String linkedinUrl;
    private String githubUrl;
    private String leetcodeUrl;
    private String cvPath;

    private Date deadline;
    private String aimRole;
    private String currentRole;

    private int totalRewardPoints;
    private int taskAssignedCount;
    private int taskCompletedCount;


    private List<USER_ROLE> userRole ;
}

