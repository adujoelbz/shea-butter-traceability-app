package com.example.sheabutterledgersystem.dto.mapper;

import com.example.sheabutterledgersystem.dto.request.UserRequest;
import com.example.sheabutterledgersystem.dto.response.UserResponse;
import com.example.sheabutterledgersystem.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = BaseMapper.class)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "passwordHash", source = "password")  // Map password to passwordHash
    User toEntity(UserRequest request);

    @Mapping(target = "fullName", expression = "java(user.getFirstName() + \" \" + user.getLastName())")
    UserResponse toResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)  // Don't update password
    @Mapping(target = "username", ignore = true)
    void updateEntity(UserRequest request, @MappingTarget User user);
}