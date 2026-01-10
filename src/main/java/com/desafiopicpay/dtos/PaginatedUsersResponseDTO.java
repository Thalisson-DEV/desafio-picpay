package com.desafiopicpay.dtos;

import java.util.List;

public record PaginatedUsersResponseDTO(
    List<UserResponseDTO> data,

    int pageNumber,
    int pageSize,
    long totalElements,
    int totalPages
) {}
