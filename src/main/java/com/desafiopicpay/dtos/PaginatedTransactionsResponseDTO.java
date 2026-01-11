package com.desafiopicpay.dtos;

import java.util.List;

public record PaginatedTransactionsResponseDTO(
        List<TransactionResponseDTO> data,

        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages
) {}
