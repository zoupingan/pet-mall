package com.anan.petadmin.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import com.anan.petadmin.dto.ProductPageQueryDTO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.anan.petadmin.entity.Product;
import com.anan.petadmin.mapper.ProductMapper;
import com.anan.petadmin.result.PageResult;
import com.anan.petadmin.service.ProductService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
    @Resource
    private ProductMapper productMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public PageResult list(ProductPageQueryDTO productPageQueryDTO) {
        int page = productPageQueryDTO.getPage();
        int pageSize = productPageQueryDTO.getPageSize();
        if (page == 0 || pageSize == 0) {
            page = 1;
            pageSize = 10;
        }
        log.info("分页参数：page={}, pageSize={}", page, pageSize);

        PageHelper.startPage(page, pageSize);

        LambdaQueryWrapper<Product> qw = new LambdaQueryWrapper<>();
        qw.like(productPageQueryDTO.getProductName() != null, Product::getProductName, productPageQueryDTO.getProductName())
                .like(productPageQueryDTO.getCategoryId() != null, Product::getCategoryId, productPageQueryDTO.getCategoryId())
                .like(productPageQueryDTO.getStatus() != null, Product::getStatus, productPageQueryDTO.getStatus());

        List<Product> productList = productMapper.selectList(qw);
        PageInfo<Product> pageInfo = new PageInfo<>(productList);
        log.info("查询结果：total={}, size={}", pageInfo.getTotal(), pageInfo.getList().size());
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public Product update(Product product) {
        log.info("更新商品：{}", product);
        product.setUpdateTime(LocalDateTime.now());
        int row = productMapper.updateById(product);
        if (row <= 0) {
            throw new RuntimeException("更新失败");
        }
        stringRedisTemplate.delete("product_detail:" + product.getId());
        stringRedisTemplate.delete("product_all");
        stringRedisTemplate.delete("product_list:" + product.getCategoryId());
        return productMapper.selectById(product.getId());
    }

    @Override
    public Integer delete(List<Long> ids) {
        int rows = productMapper.deleteByIds(ids);
        log.info("删除结果：影响行数={}", rows);
        if (rows == 0) {
            throw new RuntimeException("商品不存在或删除失败：id=" + ids);
        }
        return rows;
    }

    @Override
    public Product add(Product product) {
        int row = productMapper.insert(product);
        if (row <= 0) {
            throw new RuntimeException("添加失败");
        }
        return productMapper.selectById(product.getId());
    }

    @Override
    public List<Product> getByCategoryId(Long categoryId) {
        LambdaQueryWrapper<Product> qw = new LambdaQueryWrapper<>();
        qw.eq(Product::getCategoryId, categoryId);
        List<Product> products = productMapper.selectList(qw);
        if (products == null){
            log.info("未找到该分类下的商品");
        }
        return products;
    }

    @Override
    public Product getById(Long id) {
        return productMapper.selectById(id);
    }
}
