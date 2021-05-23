package com.java.sahil.minio.service;

import com.java.sahil.minio.dto.PageableResponseDto;
import com.java.sahil.minio.dto.UserRequestDto;
import com.java.sahil.minio.dto.UserResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    UserResponseDto create(UserRequestDto userRequestDto);

    UserResponseDto update(UserRequestDto userRequestDto, Long id);

    UserResponseDto findById(Long id);

    PageableResponseDto<List<UserResponseDto>> findAll(int page, int size);

    UserResponseDto delete(Long id);

    String uploadPhoto(MultipartFile file, Long id);

    String updatePhoto(MultipartFile file, Long id);

    void deleteUserPhoto(Long id);

    void deletePhoto(String fileName);

    byte[] getPhoto(String fileName);


}
