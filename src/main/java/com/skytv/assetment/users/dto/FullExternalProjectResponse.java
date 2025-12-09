package com.skytv.assetment.users.dto;

import java.util.List;

public record FullExternalProjectResponse (String id, String name, List<ShortUserResponse> users) {

}
