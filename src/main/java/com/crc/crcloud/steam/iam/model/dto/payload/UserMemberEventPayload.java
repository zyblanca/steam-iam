package com.crc.crcloud.steam.iam.model.dto.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserMemberEventPayload {

    private Long userId;

    private String username;

    private Long resourceId;

    private String resourceType;

    private Set<String> roleLabels;

    private String uuid;

}
