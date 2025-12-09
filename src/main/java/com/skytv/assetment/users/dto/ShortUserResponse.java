package com.skytv.assetment.users.dto;

import java.util.List;

public record ShortUserResponse(
        Long id,
        String email,
        String name
) {
}
