package com.chai.yygh.common.exception;

import com.chai.yygh.common.result.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @program: yygh
 * @author:
 * @create: 2023-01-17 11:02
 **/
@RestControllerAdvice
public class GlobalExceptionHandler {

    //异常类型
    @ExceptionHandler(Exception.class)
    public Result exception(Exception e){
        e.printStackTrace();
        return Result.fail();

    }
}
