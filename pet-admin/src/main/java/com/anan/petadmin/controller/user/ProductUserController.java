package com.anan.petadmin.controller.user;

import cn.hutool.json.JSONUtil;
import com.anan.petadmin.context.BaseContext;
import com.anan.petadmin.dto.ProductPageQueryDTO;
import com.anan.petadmin.entity.Product;
import com.anan.petadmin.result.PageResult;
import com.anan.petadmin.result.Result;
import com.anan.petadmin.service.ProductService;
import com.anan.petadmin.service.UserService;
import com.anan.petadmin.vo.UserVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/product")
@Slf4j
public class ProductUserController {
    @Resource
    private ProductService productService;
    @Resource
    private UserService userService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/list")
    public Result<List<Product>> list(@RequestParam Long categoryId) {
        String redis = stringRedisTemplate.opsForValue().get("product_list:" + categoryId);
        if (redis != null) {
            log.info("成功从redis中获取分类商品列表");
            return Result.success(JSONUtil.toList(redis, Product.class));
        }
        List<Product> product = productService.getByCategoryId(categoryId);
        stringRedisTemplate.opsForValue().set("product_list:" + categoryId, JSONUtil.toJsonStr(product), 60 * 60 * 1000);
        return Result.success(product);
    }
    @GetMapping("/detail/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        String redis = stringRedisTemplate.opsForValue().get("product_detail:" + id);
        if (redis != null) {
            log.info("成功从redis中获取商品详情");
            return Result.success(JSONUtil.toBean(redis, Product.class));
        }
        Product product = productService.getById(id);
        stringRedisTemplate.opsForValue().set("product_detail:" + id, JSONUtil.toJsonStr(product), 60 * 60 * 1000);
        return Result.success(product);
    }

    @GetMapping("/all")
    public Result<PageResult> all(ProductPageQueryDTO productPageQueryDTO) {
        String redis = stringRedisTemplate.opsForValue().get("product_all");
        if (redis != null) {
            log.info("成功从redis中获取全部商品列表");
            return Result.success(JSONUtil.toBean(redis, PageResult.class));
        }
        PageResult product = productService.list(productPageQueryDTO);
        stringRedisTemplate.opsForValue().set("product_all", JSONUtil.toJsonStr(product), 60 * 60 * 1000);
        return Result.success(product);
    }
    @GetMapping("/getUser")
    public Result<UserVO> getUser() {
        String redis = stringRedisTemplate.opsForValue().get("user_id:" + BaseContext.getCurrentId());
        if (redis != null) {
            log.info("成功从redis中获取用户信息");
            return Result.success(JSONUtil.toBean(redis, UserVO.class));
        }
        UserVO userVO = userService.getUser();
        stringRedisTemplate.opsForValue().set("user_id:" + BaseContext.getCurrentId(), JSONUtil.toJsonStr(userVO), 60 * 60 * 1000);
        return Result.success(userVO);
    }
}
