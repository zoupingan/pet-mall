package com.anan.petadmin.controller.admin;

import com.anan.petadmin.entity.Banner;
import com.anan.petadmin.result.Result;
import com.anan.petadmin.service.BannerService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/banner")
public class BannerController {
    @Resource
    private BannerService bannerService;

    @GetMapping("/list")
    public Result<List<Banner>> list() {
        List<Banner> result = bannerService.list();
        return Result.success(result);
    }

    @PutMapping("/update")
    public Result<Banner> update(@RequestBody Banner banner) {
        Banner result = bannerService.update(banner);
        return Result.success(result);
    }

    @DeleteMapping("/delete/{ids}")
    public Result<Integer> delete(@PathVariable List<Long> ids) {
        Integer row =bannerService.delete(ids);
        return Result.success(row);
    }

    @PostMapping("/add")
    public Result<Banner> add(@RequestBody Banner banner) {
        Banner result =bannerService.add(banner);
        return Result.success(result);
    }
}
