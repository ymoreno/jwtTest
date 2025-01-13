package org.jwttest.exception;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ErrorResponse {

    private Instant timestamp;
    private int code;
    private String detail;

    public ErrorResponse(int code, String detail) {
        this.timestamp = Instant.now();
        this.code = code;
        this.detail = detail;
    }

}
