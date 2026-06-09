package com.anan.petadmin.service.impl;

import com.anan.petadmin.dto.BrandPageQueryDTO;
import com.anan.petadmin.entity.Brand;
import com.anan.petadmin.mapper.BrandMapper;
import com.anan.petadmin.result.PageResult;
import com.anan.petadmin.service.BrandService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class BrandServiceImpl implements BrandService {
    @Resource
    private BrandMapper brandMapper;

    @Override
    public PageResult list(BrandPageQueryDTO brandPageQueryDTO) {
        int page = brandPageQueryDTO.getPage();
        int pageSize = brandPageQueryDTO.getPageSize();
        if (page == 0 || pageSize == 0) {
            page = 1;
            pageSize = 10;
        }
        log.info("分页参数：page={}, pageSize={}", page, pageSize);

        PageHelper.startPage(page, pageSize);

        LambdaQueryWrapper<Brand> qw = new LambdaQueryWrapper<>();
        qw.like(brandPageQueryDTO.getBrandName() != null, Brand::getBrandName, brandPageQueryDTO.getBrandName());

        List<Brand> brandList = brandMapper.selectList(qw);
        PageInfo<Brand> pageInfo = new PageInfo<>(brandList);

        log.info("查询结果：total={}, size={}", pageInfo.getTotal(), pageInfo.getList().size());
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public Brand update(Brand brand) {
        brand.setUpdateTime(LocalDateTime.now());
        int row = brandMapper.updateById(brand);
        if (row <= 0) {
            throw new RuntimeException("更新失败");
        }
        return brandMapper.selectById(brand.getId());
    }

    @Override
    public Integer delete(List<Long> ids) {
        int row = brandMapper.deleteByIds(ids);
        if (row <= 0) {
            throw new RuntimeException("删除失败");
        }
        return row;
    }

    @Override
    public Brand add(Brand brand) {
        int row = brandMapper.insert(brand);
        if (row <= 0) {
            throw new RuntimeException("添加失败");

        }
        return brandMapper.selectById(brand.getId());
    }
}
