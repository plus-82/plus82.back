package com.etplus.common;

import com.etplus.repository.domain.code.RoleType;

public record LoginUser(
    Long userId,
    String email,
    RoleType roleType
) {

}
