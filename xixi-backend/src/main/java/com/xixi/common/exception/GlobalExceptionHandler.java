package com.xixi.common.exception;

import com.xixi.common.result.R;
import com.xixi.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public R<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return R.fail(e.getCode(), e.getMessage());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage).collect(Collectors.joining("; "));
        return R.fail(ResultCode.BAD_REQUEST.getCode(), message);
    }
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleBindException(BindException e) {
        String message = e.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage).collect(Collectors.joining("; "));
        return R.fail(ResultCode.BAD_REQUEST.getCode(), message);
    }
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public R<Void> handleAuthenticationException(AuthenticationException e) {
        return R.fail(ResultCode.UNAUTHORIZED);
    }
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public R<Void> handleAccessDeniedException(AccessDeniedException e) {
        return R.fail(ResultCode.FORBIDDEN);
    }
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public R<Void> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        return R.fail(ResultCode.FILE_SIZE_EXCEEDED);
    }
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Void> handleException(Exception e) {
        // 透传真实原因，方便开发期排查；生产环境可改回 "系统繁忙"
        log.error("系统异常: ", e);
        String msg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
        return R.fail(ResultCode.INTERNAL_ERROR.getCode(), "系统异常: " + msg);
    }
}
