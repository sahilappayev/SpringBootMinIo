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

    String uploadImage(MultipartFile file, Long id);

    String updateImage(MultipartFile file, Long id);

    void deleteUserImage(Long id);

    void deleteFile(String fileName, String folder);

    byte[] getFile(String fileName, String folder);

    String uploadVideo(MultipartFile file, Long id);

    String updateVideo(MultipartFile file, Long id);

    void deleteUserVideo(Long id);

}
