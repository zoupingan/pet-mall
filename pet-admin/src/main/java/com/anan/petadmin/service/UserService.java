package com.anan.petadmin.service;

import com.anan.petadmin.dto.UserPageQueryDTO;
import com.anan.petadmin.entity.User;
import com.anan.petadmin.result.PageResult;
import com.anan.petadmin.vo.UserVO;

import java.util.List;

public interface UserService {
    PageResult list(UserPageQueryDTO userPageQueryDTO);

    UserVO update(User user);

    Integer delete(List<Long> ids);

    UserVO add(User user);
    UserVO getUser();
}
