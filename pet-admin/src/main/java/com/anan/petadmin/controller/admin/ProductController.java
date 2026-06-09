package com.anan.petadmin.controller.admin;

import com.anan.petadmin.dto.ProductPageQueryDTO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anan.petadmin.entity.Product;
import com.anan.petadmin.result.PageResult;
import com.anan.petadmin.result.Result;
import com.anan.petadmin.service.ProductService;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@RestController
@RequestMapping("/admin/product")
public class ProductController {
    @Resource
    private ProductService productService;

    @GetMapping("/list")
    public Result<PageResult> list(ProductPageQueryDTO productPageQueryDTO) {
        PageResult product = productService.list(productPageQueryDTO);
        return Result.success(product);
    }

    @PutMapping("/update")
    public Result<Product> update(@RequestBody Product product) {
        Product result = productService.update(product);
        return Result.success(result);
    }

    @DeleteMapping("/delete/{ids}")
    public Result<Integer> delete(@PathVariable List<Long> ids) {
        Integer row =productService.delete(ids);
        return Result.success(row);
    }

    @PostMapping("/add")
    public Result<Product> add(@RequestBody Product product) {
        Product result =productService.add(product);
        return Result.success(result);
    }
}
