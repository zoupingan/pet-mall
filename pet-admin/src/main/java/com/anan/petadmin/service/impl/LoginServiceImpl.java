package com.anan.petadmin.service.impl;

import com.anan.petadmin.dto.UserDTO;
import com.anan.petadmin.entity.User;
import com.anan.petadmin.mapper.LoginMapper;
import com.anan.petadmin.service.LoginService;
import com.anan.petadmin.vo.UserVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
@Slf4j
public class LoginServiceImpl implements LoginService {
    @Resource
    private LoginMapper loginMapper;

    @Override
    public String logout() {
        return "退出成功";
    }

    @Override
    public void register(UserDTO userDto) {
        User user = new User();
        BeanUtils.copyProperties(userDto, user);
        // 对密码进行md5加密
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));

        try {
            loginMapper.insert(user);
            log.info("用户注册成功");
        } catch (Exception e) {
            throw new RuntimeException("注册失败");
        }
    }

    @Override
    public UserVO login(UserDTO userDTO) {
        //获取密码的md5值
        String md5Password = DigestUtils.md5DigestAsHex(userDTO.getPassword().getBytes());
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        qw.eq(User::getUsername, userDTO.getUsername())
                .eq(User::getPassword, md5Password);
        User user = loginMapper.selectOne(qw);
        // 检查用户是否存在
        if (user == null) {
            throw new RuntimeException("没有找到该用户！");
        }
        // md5密码比对
        if (!user.getPassword().equals(md5Password)) {
            throw new RuntimeException("密码错误！");
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }
}
