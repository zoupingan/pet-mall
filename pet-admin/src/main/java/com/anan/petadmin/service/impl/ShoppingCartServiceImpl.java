package com.anan.petadmin.service.impl;

import com.anan.petadmin.context.BaseContext;
import com.anan.petadmin.dto.ShoppingCartDTO;
import com.anan.petadmin.entity.Product;
import com.anan.petadmin.entity.ShoppingCart;
import com.anan.petadmin.mapper.ProductMapper;
import com.anan.petadmin.mapper.ShoppingCartMapper;
import com.anan.petadmin.service.ShoppingCartService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Resource
    private ShoppingCartMapper shoppingCartMapper;
    @Resource
    private ProductMapper productMapper;

    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        if (shoppingCartDTO.getProductId() == null) {
            throw new IllegalArgumentException("商品ID不能为空");
        }

        Integer number = shoppingCartDTO.getNumber();
        if (number == null) {
            number = 1;
        }
        if (number <= 0) {
            throw new IllegalArgumentException("加入购物车的商品数量必须大于0");
        }

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setProductId(shoppingCartDTO.getProductId());
        shoppingCart.setUserId(BaseContext.getCurrentId());

        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId, shoppingCart.getUserId())
                .eq(ShoppingCart::getProductId, shoppingCart.getProductId());
        ShoppingCart sc = shoppingCartMapper.selectOne(qw);
        // 已有商品时累加本次选择的数量。
        if (sc != null) {
            sc.setNumber(sc.getNumber() + number);
            shoppingCartMapper.updateById(sc);
            return;
        }
        // 没有商品时按本次选择的数量新增。
        Product product = productMapper.selectById(shoppingCart.getProductId());
        if (product == null) {
            throw new IllegalArgumentException("商品不存在");
        }
        shoppingCart.setNumber(number);
        shoppingCart.setName(product.getProductName());
        shoppingCart.setImage(product.getImageUrl());
        shoppingCart.setAmount(product.getPrice());
        shoppingCartMapper.insert(shoppingCart);
    }

    @Override
    public List<ShoppingCart> showShoppingCart() {
        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCartMapper.selectList(qw);
        return list;
    }

    @Override
    public void clean() {
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId, currentId);
        shoppingCartMapper.delete(qw);
    }

    @Override
    public void subtractShoppingCart(ShoppingCartDTO shoppingCartDTO) {

        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId, BaseContext.getCurrentId())
                .eq(ShoppingCart::getProductId, shoppingCartDTO.getProductId());
        ShoppingCart sc = shoppingCartMapper.selectOne(qw);
        Long shoppingCartId = sc.getId();
        Integer number = sc.getNumber();
        if (number > 1){
            sc.setNumber(sc.getNumber() - 1);
            shoppingCartMapper.updateById(sc);
            return;
        }
        shoppingCartMapper.deleteById(shoppingCartId);
    }

    @Override
    public void clearProdcutById(ShoppingCartDTO shoppingCartDTO) {
        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId, BaseContext.getCurrentId())
                .eq(ShoppingCart::getProductId, shoppingCartDTO.getProductId());
        ShoppingCart sc = shoppingCartMapper.selectOne(qw);
        Long shoppingCartId = sc.getId();
        shoppingCartMapper.deleteById(shoppingCartId);
    }
}
