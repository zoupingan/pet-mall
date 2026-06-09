package com.anan.petadmin.controller.admin;

import com.anan.petadmin.dto.UserPageQueryDTO;
import com.anan.petadmin.entity.User;
import com.anan.petadmin.result.PageResult;
import com.anan.petadmin.result.Result;
import com.anan.petadmin.service.UserService;
import com.anan.petadmin.vo.UserVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/user")
public class UserController {
    @Resource
    private UserService userService;
    @GetMapping("/list")
    public Result<PageResult> list(UserPageQueryDTO userPageQueryDTO) {
        PageResult result = userService.list(userPageQueryDTO);
        return Result.success(result);
    }

    @PutMapping("/update")
    public Result<UserVO> update(@RequestBody User user) {
        UserVO result = userService.update(user);
        return Result.success(result);
    }

    @DeleteMapping("/delete/{ids}")
    public Result<Integer> delete(@PathVariable List<Long> ids) {
        Integer row =userService.delete(ids);
        return Result.success(row);
    }

    @PostMapping("/add")
    public Result<UserVO> add(@RequestBody User user) {
        UserVO result = userService.add(user);
        return Result.success(result);
    }
}
