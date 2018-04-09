package com.allen;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;

/**
 * Author   yang_tao@<yangtao.letzgo.com.cn>
 * Date     2017-10-24 12:40
 * Version  1.0
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    private static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseData handler(Exception e) {
        logger.error(ExceptionStackTraceUtils.getStackTrace(e));
        if (e instanceof HttpRequestMethodNotSupportedException) {
            return ResponseData.VALID_ERROR("HttpRequestMethodNotSupportedException");
        }
        if (e instanceof HttpMediaTypeNotSupportedException) {
            return ResponseData.VALID_ERROR("HttpMediaTypeNotSupportedException");
        }
        if (e instanceof HttpMediaTypeNotAcceptableException) {
            return ResponseData.VALID_ERROR("HttpMediaTypeNotAcceptableException");
        }
        if (e instanceof MissingServletRequestParameterException) {
            return ResponseData.VALID_ERROR("MissingServletRequestParameterException");
        }
        if (e instanceof ServletRequestBindingException) {
            return ResponseData.VALID_ERROR("ServletRequestBindingException");
        }
        if (e instanceof ConversionNotSupportedException) {
            return ResponseData.VALID_ERROR("ConversionNotSupportedException");
        }
        if (e instanceof TypeMismatchException) {
            return ResponseData.VALID_ERROR("TypeMismatchException");
        }
        if (e instanceof HttpMessageNotReadableException) {
            return ResponseData.VALID_ERROR("HttpMessageNotReadableException");
        }
        if (e instanceof HttpMessageNotWritableException) {
            return ResponseData.VALID_ERROR("HttpMessageNotWritableException");
        }
        if (e instanceof MethodArgumentNotValidException) {
            BindingResult result = ((MethodArgumentNotValidException) e).getBindingResult();
            if (result.hasErrors()) {
                List<FieldError> errors = result.getFieldErrors();
                if (errors != null && errors.size() > 0) {
                    return ResponseData.VALID_ERROR(errors.get(0).getDefaultMessage());
                }
            }
        }
        if (e instanceof MissingServletRequestPartException) {
            return ResponseData.VALID_ERROR("MissingServletRequestPartException");
        }
        if (e instanceof BindException) {
            BindingResult result = ((BindException) e).getBindingResult();
            if (result.hasErrors()) {
                List<FieldError> errors = result.getFieldErrors();
                if (errors != null && errors.size() > 0) {
                    return ResponseData.VALID_ERROR(errors.get(0).getDefaultMessage());
                }
            }
        }
        if (e instanceof NoHandlerFoundException) {
            return ResponseData.VALID_ERROR("NoHandlerFoundException");
        }
        if (e instanceof BizDataException) {
            return ResponseData.BIZ_ERROR(StringUtils.isBlank(e.getMessage()) ? "BizDataException" : e.getMessage());
        }
        return ResponseData.SYSTEM_ERROR();
    }

}