package com.anan.petadmin.controller.admin;

import com.anan.petadmin.dto.BrandPageQueryDTO;
import com.anan.petadmin.entity.Brand;
import com.anan.petadmin.result.PageResult;
import com.anan.petadmin.result.Result;
import com.anan.petadmin.service.BrandService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/brand")
public class BrandController {
    @Resource
    private BrandService brandService;
    @GetMapping("/list")
    public Result<PageResult> list(BrandPageQueryDTO brandPageQueryDTO) {
        PageResult result = brandService.list(brandPageQueryDTO);
        return Result.success(result);
    }

    @PutMapping("/update")
    public Result<Brand> update(@RequestBody Brand brand) {
        Brand result = brandService.update(brand);
        return Result.success(result);
    }

    @DeleteMapping("/delete/{ids}")
    public Result<Integer> delete(@PathVariable List<Long> ids) {
        Integer row = brandService.delete(ids);
        return Result.success(row);
    }

    @PostMapping("/add")
    public Result<Brand> add(@RequestBody Brand brand) {
        Brand result = brandService.add(brand);
        return Result.success(result);
    }
}
