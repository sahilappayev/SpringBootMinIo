package com.java.sahil.minio.mapper;

import com.java.sahil.minio.dto.UserRequestDto;
import com.java.sahil.minio.dto.UserResponseDto;
import com.java.sahil.minio.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "spring")
public interface UserMapper {

    UserResponseDto toUserDto(User user);

    User toUserEntity(UserRequestDto userRequestDto);

    List<UserResponseDto> toUserDtoList(List<User> userList);

}
