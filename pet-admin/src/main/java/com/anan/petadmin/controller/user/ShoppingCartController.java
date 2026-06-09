package com.anan.petadmin.controller.user;

import com.anan.petadmin.dto.ShoppingCartDTO;
import com.anan.petadmin.entity.ShoppingCart;
import com.anan.petadmin.result.Result;
import com.anan.petadmin.service.ShoppingCartService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
public class ShoppingCartController {
    @Resource
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public Result addShoppingCart(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        shoppingCartService.addShoppingCart(shoppingCartDTO);
        return Result.success();
    }
    @PostMapping("/subtract")
    public Result subtractShoppingCart(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        shoppingCartService.subtractShoppingCart(shoppingCartDTO);
        return Result.success();
    }
    @PostMapping("/subtractAll")
    public Result clearProduct(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        shoppingCartService.clearProdcutById(shoppingCartDTO);
        return Result.success();
    }
    @GetMapping("/list")
    public Result<List<ShoppingCart>> list() {
        List<ShoppingCart> list = shoppingCartService.showShoppingCart();
        return Result.success(list);
    }

    @DeleteMapping("/clean")
    public Result clean(){
        shoppingCartService.clean();
        return Result.success();
    }
}

