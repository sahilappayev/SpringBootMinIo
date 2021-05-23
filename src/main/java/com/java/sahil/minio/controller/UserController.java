package com.java.sahil.minio.controller;

import com.java.sahil.minio.dto.PageableResponseDto;
import com.java.sahil.minio.dto.UserRequestDto;
import com.java.sahil.minio.dto.UserResponseDto;
import com.java.sahil.minio.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @ApiOperation(value = "Get User List as Pageable")
    @SwaggerApiResponse
    public ResponseEntity<PageableResponseDto<List<UserResponseDto>>> getAllPaged(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size) {
        return ResponseEntity.status(200).body(userService.findAll(page, size));
    }


    @GetMapping("{id}")
    @ApiOperation(value = "Get User by Id")
    @SwaggerApiResponse
    public ResponseEntity<UserResponseDto> getById(@PathVariable("id") Long id) {
        return ResponseEntity.status(200).body(userService.findById(id));
    }

    @PostMapping
    @ApiOperation(value = "Add User")
    @SwaggerApiResponse
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.status(200).body(userService.create(userRequestDto));
    }

    @PutMapping("{id}")
    @ApiOperation(value = "Update User")
    @SwaggerApiResponse
    public ResponseEntity<UserResponseDto> update(@PathVariable("id") Long id,
                                                  @Valid @RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.status(200).body(userService.update(userRequestDto, id));
    }

    @DeleteMapping("{id}")
    @ApiOperation(value = "Delete User")
    @SwaggerApiResponse
    public ResponseEntity<UserResponseDto> delete(@PathVariable("id") Long id) {
        return ResponseEntity.status(200).body(userService.delete(id));
    }


    @PostMapping("/image/{id}")
    @ApiOperation(value = "Add User Photo")
    public ResponseEntity<String> createImage(@PathVariable Long id, @Valid @RequestParam MultipartFile file) {
        return ResponseEntity.status(200).body(userService.uploadPhoto(file, id));
    }

    @PutMapping("/image/{id}")
    @ApiOperation(value = "Update User Photo")
    public ResponseEntity<String> updateImage(@PathVariable Long id, @Valid @RequestParam MultipartFile file) {
        return ResponseEntity.status(200).body(userService.updatePhoto(file, id));
    }

    @GetMapping(value = "/image/{fileName}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    @ApiOperation(value = "Get User Photo")
    public byte[] getImage(@PathVariable("fileName") String fileName) {
        return userService.getPhoto(fileName);
    }

    @DeleteMapping("/image/{id}")
    public void deleteUserImage(@PathVariable("id") Long id) {
        userService.deleteUserPhoto(id);
    }

}
