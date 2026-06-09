package com.anan.petadmin.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class BrandPageQueryDTO implements Serializable {

    // 品牌名称
    private String brandName;
    // 页码
    private int page;
    // 每页显示记录数
    private int pageSize;

}
