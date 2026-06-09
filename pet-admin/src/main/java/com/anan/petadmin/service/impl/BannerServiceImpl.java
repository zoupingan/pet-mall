package com.anan.petadmin.service.impl;

import com.anan.petadmin.entity.Banner;
import com.anan.petadmin.mapper.BannerMapper;
import com.anan.petadmin.service.BannerService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class BannerServiceImpl implements BannerService {
    @Resource
    private BannerMapper bannerMapper;

    @Override
    public List<Banner> list() {
        return bannerMapper.selectList(null);
    }

    @Override
    public Banner update(Banner banner) {
        log.info("更新商品：{}", banner);
        banner.setUpdateTime(LocalDateTime.now());
        int row = bannerMapper.updateById(banner);
        if (row <= 0) {
            throw new RuntimeException("更新失败");
        }
        return bannerMapper.selectById(banner.getId());
    }

    @Override
    public Integer delete(List<Long> ids) {
        int rows = bannerMapper.deleteByIds(ids);
        log.info("删除结果：影响行数={}", rows);
        if (rows == 0) {
            throw new RuntimeException("商品不存在或删除失败：id=" + ids);
        }
        return rows;
    }

    @Override
    public Banner add(Banner banner) {
        int row = bannerMapper.insert(banner);
        if (row <= 0) {
            throw new RuntimeException("添加失败");
        }
        return bannerMapper.selectById(banner.getId());
    }
}
