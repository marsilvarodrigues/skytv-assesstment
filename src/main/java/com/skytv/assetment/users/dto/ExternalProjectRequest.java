package com.skytv.assetment.users.dto;

import jakarta.validation.constraints.NotBlank;

public record ExternalProjectRequest(
        String id, @NotBlank String name
) {
}
