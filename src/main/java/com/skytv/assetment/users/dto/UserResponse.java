package com.skytv.assetment.users.dto;

import java.util.List;

public record UserResponse(
        Long id,
        String email,
        String name,
        List<ExternalProjectResponse> externalProjects
) {
}
