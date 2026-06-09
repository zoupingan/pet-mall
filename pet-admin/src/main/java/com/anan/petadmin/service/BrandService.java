package com.anan.petadmin.service;

import com.anan.petadmin.dto.BrandPageQueryDTO;
import com.anan.petadmin.entity.Brand;
import com.anan.petadmin.result.PageResult;

import java.util.List;

public interface BrandService {
    PageResult list(BrandPageQueryDTO brandPageQueryDTO);

    Brand update(Brand brand);

    Integer delete(List<Long> ids);

    Brand add(Brand brand);
}
