package com.anan.petadmin.service;

import com.anan.petadmin.dto.UserDTO;
import com.anan.petadmin.vo.UserVO;

public interface LoginService {

    String logout();

    void register(UserDTO userDto);

    UserVO login(UserDTO userDTO);
}
