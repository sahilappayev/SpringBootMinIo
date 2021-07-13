package com.java.sahil.minio.controller;

import com.java.sahil.minio.dto.FileDto;
import com.java.sahil.minio.dto.PageableResponseDto;
import com.java.sahil.minio.dto.UserRequestDto;
import com.java.sahil.minio.dto.UserResponseDto;
import com.java.sahil.minio.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    @Value("${minio.image-folder}")
    private String imageFolder;
    @Value("${minio.video-folder}")
    private String videoFolder;

    @GetMapping
    @ApiOperation(value = "Get User List as Pageable")
    @SwaggerApiResponse
    public ResponseEntity<PageableResponseDto<List<UserResponseDto>>> getAllPaged(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findAll(page, size));
    }


    @GetMapping("{id}")
    @ApiOperation(value = "Get User by Id")
    @SwaggerApiResponse
    public ResponseEntity<UserResponseDto> getById(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findById(id));
    }

    @PostMapping
    @ApiOperation(value = "Add User")
    @SwaggerApiResponse
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(userRequestDto));
    }

    @PutMapping("{id}")
    @ApiOperation(value = "Update User")
    @SwaggerApiResponse
    public ResponseEntity<UserResponseDto> update(@PathVariable("id") Long id,
                                                  @Valid @RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.update(userRequestDto, id));
    }

    @DeleteMapping("{id}")
    @ApiOperation(value = "Delete User")
    @SwaggerApiResponse
    public ResponseEntity<UserResponseDto> delete(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.delete(id));
    }

    /**
     * FILE URIs
     */
    @PostMapping(value = "/image/{id}")
    @ApiOperation(value = "Add User Image")
    public ResponseEntity<FileDto> createImage(@PathVariable Long id, @Valid @RequestParam MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new FileDto(userService.uploadImage(file, id)));
    }

    @PutMapping(value = "/image/{id}")
    @ApiOperation(value = "Update User Image")
    public ResponseEntity<FileDto> updateImage(@PathVariable Long id, @Valid @RequestParam MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new FileDto(userService.updateImage(file, id)));
    }

    @GetMapping(value = "/image/{fileName}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    @ApiOperation(value = "Get User Image")
    @ResponseStatus(HttpStatus.OK)
    public byte[] getImage(@PathVariable("fileName") String fileName) {
        return userService.getFile(fileName, imageFolder);
    }

    @DeleteMapping("/image/{id}")
    @ApiOperation(value = "Delete User Image")
    public void deleteUserImage(@PathVariable("id") Long id) {
        userService.deleteUserImage(id);
    }

    @PostMapping(value = "/video/{id}")
    @ApiOperation(value = "Add User Video")
    public ResponseEntity<FileDto> createVideo(@PathVariable Long id, @Valid @RequestParam MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new FileDto(userService.uploadVideo(file, id)));
    }

    @PutMapping(value = "/video/{id}")
    @ApiOperation(value = "Update User Video")
    public ResponseEntity<FileDto> updateVideo(@PathVariable Long id, @Valid @RequestParam MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new FileDto(userService.updateVideo(file, id)));
    }

    @SneakyThrows
    @GetMapping(value = "/video/{fileName}")
    @ApiOperation(value = "Get User Video")
    public ResponseEntity<InputStreamResource> getVideo(@PathVariable("fileName") String fileName) {
        byte[] videoData = userService.getFile(fileName, videoFolder);
        try (InputStream file = new ByteArrayInputStream(videoData)) {
            Path path = new File(fileName).toPath();
            String mimeType = Files.probeContentType(path);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Ranges", "bytes");
            headers.set("Content-Type", mimeType);
            headers.set("Content-Range", "bytes 50-1025/17839845");
            headers.set("Content-Length", String.valueOf(videoData.length));
            return new ResponseEntity<>(new InputStreamResource(file), headers, HttpStatus.OK);
        }
    }

    @DeleteMapping("/video/{id}")
    @ApiOperation(value = "Delete User Video")
    public void deleteUserVideo(@PathVariable("id") Long id) {
        userService.deleteUserVideo(id);
    }

}
