package com.me.linerublinejai.exceptions;

import com.me.linerublinejai.models.responses.ErrorResponse;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

@Data
public class CustomErrorException extends Exception {

    private HttpStatus httpStatus;

    private List<ErrorResponse> errors = new ArrayList<>();

    public CustomErrorException(ErrorResponse error, HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        this.errors.add(error);
    }

    public CustomErrorException(List<ErrorResponse> errors, HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        this.errors = errors;
    }
}
