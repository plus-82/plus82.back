package com.etplus.repository;

import com.etplus.controller.dto.SearchUserDTO;
import com.etplus.vo.UserVO;
import org.springframework.data.domain.Slice;

public interface UserRepositoryCustom {
    Slice<UserVO> findAllUsers(SearchUserDTO dto);
}