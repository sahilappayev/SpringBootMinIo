package com.java.sahil.minio.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

import static net.logstash.logback.argument.StructuredArguments.kv;

@RestControllerAdvice
@Slf4j
@PropertySource("classpath:messages.properties")
public class ErrorHandler extends ResponseEntityExceptionHandler {
    @Value("${error.notFound}")
    private String notFound;
    @Value("${error.extensionNotAcceptable}")
    private String extensionError;


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        var ref = new Object() {
            String fieldName = null;
            String message = null;
        };
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            ref.fieldName = ((FieldError) error).getField();
            ref.message = error.getDefaultMessage();
            errors.put(ref.fieldName, ref.message);
        });
        errors.forEach((fieldName, message) -> {
            log.error("Api error, {}, message: {} ",
                    kv("errorCode", 1011), fieldName + " - " + message);
        });
        return new ResponseEntity<>(new ApiError(1011, ref.fieldName + " - " + ref.message),
                HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        log.error("Api error, {}, message: {} ",
                kv("errorCode", 1011), ex.getMessage());
        return new ResponseEntity<>(new ApiError(1011, ex.getCause().getMessage()),
                HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(ExtensionNotAcceptableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError extensionError(final Exception e) {
        log.error("Api error, {}, message: {}, {} ",
                kv("errorCode", 1001), e.getMessage(), extensionError);
        return new ApiError(1001, e.getMessage() + extensionError);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError entityNotFound(final Exception e) {
        log.error("Api error, {}, message: {}, {} ",
                kv("errorCode", 1002), e.getMessage(), notFound);
        return new ApiError(1002, e.getMessage() + notFound);
    }

    @ExceptionHandler(FileCantUploadException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError fileUploadError(final Exception e) {
        log.error("Api error, {}, message: {}",
                kv("errorCode", 1001), e.getMessage());
        return new ApiError(1001, e.getMessage());
    }

}