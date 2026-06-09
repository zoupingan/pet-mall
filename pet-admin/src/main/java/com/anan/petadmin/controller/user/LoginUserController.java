package com.anan.petadmin.controller.user;

import com.anan.petadmin.dto.UserDTO;
import com.anan.petadmin.properties.JwtProperties;
import com.anan.petadmin.result.Result;
import com.anan.petadmin.service.LoginService;
import com.anan.petadmin.utils.JwtUtil;
import com.anan.petadmin.vo.UserVO;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
class LoginUserController {
    @Resource
    private JwtProperties jwtProperties;

    @Resource
    private LoginService loginService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/login")
    public Result<UserVO> login(@RequestBody UserDTO userDTO) {
        UserVO userVO = loginService.login(userDTO);
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", userVO.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims);
        userVO.setToken(token);
        stringRedisTemplate.opsForValue().set("token_user_id:" + userVO.getId(), token, jwtProperties.getUserTtl());
        return Result.success(userVO);
    }
    @PostMapping("/logout")
    public Result logout() {
        return Result.success();
    }
    @PostMapping("/register")
    public Result register(@RequestBody UserDTO userDTO) {
        loginService.register(userDTO);
        return Result.success();
    }
}