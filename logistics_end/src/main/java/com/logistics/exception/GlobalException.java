package com.logistics.exception;

import com.logistics.entity.Result;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(Exception.class)
    public Result allExceptionCatch(Exception e) throws Exception {
        if(e instanceof AccessDeniedException ||e instanceof AuthenticationException){
            throw e;
        }
        return Result.error(e.getMessage()!=null||!"".equals(e.getMessage())?e.getMessage():"系统发生了异常");
    }
}
