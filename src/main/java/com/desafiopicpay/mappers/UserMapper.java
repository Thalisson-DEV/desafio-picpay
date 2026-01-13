package com.desafiopicpay.mappers;

import com.desafiopicpay.domain.role.Role;
import com.desafiopicpay.domain.user.User;
import com.desafiopicpay.dtos.PaginatedUsersResponseDTO;
import com.desafiopicpay.dtos.UserRequestDTO;
import com.desafiopicpay.dtos.UserResponseDTO;
import lombok.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper()
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    User toUserEntity(UserRequestDTO dto);

    UserResponseDTO toUserResponseDTO(User entity);

    @Mapping(target = "id", ignore = true)
    void updateUserFromDTO(UserRequestDTO dto, @MappingTarget User entity);

    default PaginatedUsersResponseDTO toPaginatedResponseDTO(Page<@NonNull User> page) {
        return new PaginatedUsersResponseDTO(
                page.getContent()
                        .stream()
                        .map(this::toUserResponseDTO)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
