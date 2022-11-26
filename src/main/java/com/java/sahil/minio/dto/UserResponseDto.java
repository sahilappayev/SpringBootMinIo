package com.java.sahil.minio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    private Long id;
    private String name;
    private String surname;
    private String photo;
    private String video;
    private String resume;

    private Map<String, String> photos;

}
