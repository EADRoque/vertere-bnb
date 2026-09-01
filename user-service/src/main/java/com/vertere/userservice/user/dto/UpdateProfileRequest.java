package com.vertere.userservice.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateProfileRequest (
    
    @NotBlank
    String fullName,

    String phone,

    String avatarUrl
) {}
