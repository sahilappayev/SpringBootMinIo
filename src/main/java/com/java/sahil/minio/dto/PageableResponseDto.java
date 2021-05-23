package com.java.sahil.minio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageableResponseDto<T> implements Serializable {

    private T responseDto;
    private long totalElementsOnDatabase;
    private long returnedElementsToUI;
}
