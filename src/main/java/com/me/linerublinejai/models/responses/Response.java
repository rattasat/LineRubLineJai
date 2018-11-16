package com.me.linerublinejai.models.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {

    private T data;

    public List<ErrorResponse> errors = new ArrayList();


    public Response(List<ErrorResponse> errors) {
        this.errors = errors;
    }

    public Response(T data) {
        this.data = data;
    }

    public Response(T data, List<ErrorResponse> errors) {
        this.data = data;
        this.errors = errors;
    }

    public Response() { }
}
