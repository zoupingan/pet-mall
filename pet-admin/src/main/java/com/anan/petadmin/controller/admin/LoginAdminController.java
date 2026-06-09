package com.anan.petadmin.controller.admin;

import com.anan.petadmin.properties.JwtProperties;
import com.anan.petadmin.result.Result;
import com.anan.petadmin.utils.JwtUtil;
import com.anan.petadmin.vo.UserVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anan.petadmin.dto.UserDTO;

import com.anan.petadmin.service.LoginService;

import jakarta.annotation.Resource;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
class LoginAdminController {
    @Resource
    private JwtProperties jwtProperties;

    @Resource
    private LoginService loginService;

    @PostMapping("/login")
    public Result<UserVO> login(@RequestBody UserDTO userDTO) {
        UserVO userVO = loginService.login(userDTO);
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", userVO.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);
        userVO.setToken(token);
        return Result.success(userVO);
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody UserDTO userDto) {
        loginService.register(userDto);
        return Result.success("注册成功");
    }
}