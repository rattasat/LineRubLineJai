package com.me.linerublinejai.types;

import com.me.linerublinejai.utils.ErrorMessage;

public enum ErrorType {

    LINE_USER_NOT_FOUND("4001", ErrorMessage.LINE_USER_NOT_NOT_FOUND),
    SERVER_ERROR("5001", ErrorMessage.SERVER_ERROR);

    private String code;
    private String message;

    ErrorType(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

}
