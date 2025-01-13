package org.jwtTest.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserRequest {

    private String name;

    private String email;

    private String password;
    private List<Phone> phones;

    @Data
    @Builder
    public static class Phone {
        private long number;
        private int citycode;
        private String contrycode;
    }
}
