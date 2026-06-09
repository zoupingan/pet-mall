package com.anan.petadmin.service;

import com.anan.petadmin.entity.Category;
import com.anan.petadmin.result.PageResult;

import java.util.List;

public interface CategoryService {
    PageResult list(Integer page, Integer pageSize);

    Category update(Category category);

    Integer delete(List<Long> ids);

    Category add(Category category);

    List<Category> getAllCategory();
}
