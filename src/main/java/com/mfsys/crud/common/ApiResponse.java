package com.mfsys.crud.common;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;


@Getter
@Builder
public class ApiResponse<T> {
private String code;
private  T data;
private  String message;
private  boolean success;
private Instant timestamp;


public static <T> ApiResponse<T> success(String code, String message, T data){
    return  ApiResponse.<T>builder().code(code)
            .message(message)
            .data(data)
            .success(true)
            .timestamp(Instant.now())
            .build();
}
}
