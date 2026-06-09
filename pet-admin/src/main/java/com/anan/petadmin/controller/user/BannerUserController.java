package com.anan.petadmin.controller.user;

import com.anan.petadmin.entity.Banner;
import com.anan.petadmin.result.Result;
import com.anan.petadmin.service.BannerService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/banner")
public class BannerUserController {
    @Resource
    private BannerService bannerService;

    @GetMapping("/list")
    public Result<List<Banner>> list() {
        List<Banner> result = bannerService.list();
        return Result.success(result);
    }

}
