package com.anan.petadmin.service;

import com.anan.petadmin.dto.ProductPageQueryDTO;
import com.anan.petadmin.entity.Product;
import com.anan.petadmin.result.PageResult;

import java.util.List;

public interface ProductService {

    PageResult list(ProductPageQueryDTO productPageQueryDTO);

    Product update(Product product);

    Integer delete(List<Long> ids);

    Product add(Product product);

    List<Product> getByCategoryId(Long categoryId);

    Product getById(Long id);
}
