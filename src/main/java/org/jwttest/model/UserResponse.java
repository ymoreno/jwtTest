package org.jwttest.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
public class UserResponse {

    private UUID id;
    private Date created;
    private Date lastLogin;
    private String token;
    private boolean isActive;
}
