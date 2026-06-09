package com.anan.petadmin.service.impl;

import com.anan.petadmin.entity.Category;
import com.anan.petadmin.mapper.CategoryMapper;
import com.anan.petadmin.result.PageResult;
import com.anan.petadmin.service.CategoryService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    @Resource
    private CategoryMapper categoryMapper;

    @Override
    public PageResult list(Integer page, Integer pageSize) {
        log.info("分页参数：page={}, pageSize={}", page, pageSize);

        PageHelper.startPage(page, pageSize);

        List<Category> categoryList = categoryMapper.selectList(null);
        PageInfo<Category> pageInfo = new PageInfo<>(categoryList);
        log.info("查询结果：total={}, size={}", pageInfo.getTotal(), pageInfo.getList().size());
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public Category update(Category category) {
        category.setUpdateTime(LocalDateTime.now());
        int row = categoryMapper.updateById(category);
        if (row <= 0) {
            throw new RuntimeException("更新失败");
        }
        return categoryMapper.selectById(category.getId());
    }

    @Override
    public Integer delete(List<Long> ids) {
        int row = categoryMapper.deleteByIds(ids);
        if (row <= 0) {
            throw new RuntimeException("删除失败");
        }
        return row;
    }

    @Override
    public Category add(Category category) {
        int row = categoryMapper.insert(category);
        if (row <= 0) {
            throw new RuntimeException("添加失败");
        }
        return categoryMapper.selectById(category.getId());
    }

    @Override
    public List<Category> getAllCategory() {
        return categoryMapper.selectList(null);
    }
}
