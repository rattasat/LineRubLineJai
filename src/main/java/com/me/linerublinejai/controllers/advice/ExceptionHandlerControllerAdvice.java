package com.me.linerublinejai.controllers.advice;

import com.me.linerublinejai.exceptions.CustomErrorException;
import com.me.linerublinejai.models.responses.ErrorResponse;
import com.me.linerublinejai.models.responses.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionHandlerControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<?> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(new Response<ErrorResponse>(
                ex.getBindingResult().getAllErrors()
                        .stream()
                        .map(error -> new ErrorResponse(error.getCode(), error.getDefaultMessage()))
                        .collect(Collectors.toList())));
    }

    @ExceptionHandler(CustomErrorException.class)
    @ResponseBody
    public ResponseEntity<Response> actErrorExceptionHandler(CustomErrorException ex) {
        return new ResponseEntity<>(new Response(ex.getErrors()), ex.getHttpStatus());
    }

}
