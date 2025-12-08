package com.skytv.assetment.users.dto;

import jakarta.validation.constraints.NotBlank;

public record ExternalProjectRequest(
        @NotBlank String name
) {
}
