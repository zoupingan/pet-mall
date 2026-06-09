package com.anan.petadmin.controller.user;

import cn.hutool.json.JSONUtil;
import com.anan.petadmin.entity.Category;
import com.anan.petadmin.result.Result;
import com.anan.petadmin.service.CategoryService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user/category")
@Slf4j
public class CategoryUserController {
    @Resource
    private CategoryService categoryService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @GetMapping("/list")
    public Result<List<Category>> list() {
        String redis = stringRedisTemplate.opsForValue().get("category_list");
        if (redis != null) {
            log.info("成功从redis中获取分类列表");
            return Result.success(JSONUtil.toList(redis, Category.class));
        }
        List<Category> result = categoryService.getAllCategory();
        stringRedisTemplate.opsForValue().set("category_list", JSONUtil.toJsonStr(result), 60 * 60,TimeUnit.MINUTES);
        log.info("成功缓存分类列表到redis");
        return Result.success(result);
    }
}
