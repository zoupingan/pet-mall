package com.anan.petadmin.service;

import com.anan.petadmin.entity.Banner;

import java.util.List;

public interface BannerService {

    List<Banner> list();

    Banner update(Banner banner );

    Integer delete(List<Long> ids);

    Banner add(Banner banner);

}
