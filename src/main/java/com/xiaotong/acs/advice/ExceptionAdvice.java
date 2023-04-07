package com.xiaotong.acs.advice;

import com.xiaotong.acs.domain.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(Exception.class)
    public Result exceptionHandler(Exception e) {
        return Result.error(e.getMessage());
    }

}