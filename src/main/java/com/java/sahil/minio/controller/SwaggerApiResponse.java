package com.java.sahil.minio.controller;

import com.java.sahil.minio.error.ApiError;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ApiResponses(value = {
        @ApiResponse(code = 500, message = "Server Error", response = ApiError.class),
        @ApiResponse(code = 404, message = "Not Found", response = ApiError.class),
})
public @interface SwaggerApiResponse {

}
