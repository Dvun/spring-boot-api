package com.codeapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_MODIFIED)
public class NotModifiedException extends RuntimeException {

    public NotModifiedException(String message) {
        super(message);
    }
}
