package org.jwttest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    private String name;

    private String email;

    private String password;
    private List<Phone> phones;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Phone {
        private long number;
        private int citycode;
        private String contrycode;
    }
}
