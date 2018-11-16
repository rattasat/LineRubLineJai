package com.me.linerublinejai.models.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private String code;

    private String message;

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
