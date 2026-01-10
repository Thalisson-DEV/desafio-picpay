package com.desafiopicpay.domain.role;

import lombok.Getter;

@Getter
public enum RoleType {

    BASIC(1L),
    ADMIN(2L);

    final long id;

    RoleType(long id) {
        this.id = id;
    }
}
