package com.anan.petadmin.service.impl;

import com.anan.petadmin.context.BaseContext;
import com.anan.petadmin.dto.UserPageQueryDTO;
import com.anan.petadmin.entity.User;
import com.anan.petadmin.mapper.UserMapper;
import com.anan.petadmin.result.PageResult;
import com.anan.petadmin.service.UserService;
import com.anan.petadmin.vo.UserVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;
    @Override
    public PageResult list(UserPageQueryDTO userPageQueryDTO) {
        int page = userPageQueryDTO.getPage();
        int pageSize = userPageQueryDTO.getPageSize();
        log.info("分页参数：page={}, pageSize={}", page, pageSize);

        PageHelper.startPage(page, pageSize);

        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        qw.like(userPageQueryDTO.getUsername() != null, User::getUsername, userPageQueryDTO.getUsername())
                .like(userPageQueryDTO.getStatus()!=null, User::getStatus, userPageQueryDTO.getStatus());

        List<User> userList = userMapper.selectList(qw);
        PageInfo<User> pageInfo = new PageInfo<>(userList);
        log.info("查询结果：total={}, size={}", pageInfo.getTotal(), pageInfo.getList().size());
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public UserVO update(User user) {
        log.info("更新用户信息：{}", user);
        user.setUpdateTime(LocalDateTime.now());
        int row = userMapper.updateById(user);
        if (row <= 0){
            throw new RuntimeException("更新失败");
        }
        User newUser = userMapper.selectById(user.getId());
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(newUser, userVO);
        return userVO;
    }

    @Override
    public Integer delete(List<Long> ids) {
        int row = userMapper.deleteByIds(ids);
        if (row <= 0){
            throw new RuntimeException("删除失败");
        }
        return row;
    }

    @Override
    public UserVO add(User user) {
        int row = userMapper.insert(user);
        if (row <=0){
            throw new RuntimeException("添加失败");
        }
        User newUser = userMapper.selectById(user.getId());
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(newUser, userVO);
        return userVO;
    }

    @Override
    public UserVO getUser() {
        User user = userMapper.selectById(BaseContext.getCurrentId());
        if (user == null){
            throw new RuntimeException("用户不存在");
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }
}
