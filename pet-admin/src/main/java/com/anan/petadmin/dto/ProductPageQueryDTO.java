package com.anan.petadmin.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProductPageQueryDTO implements Serializable {

    // 商品名称
    private String productName;
    //商品分类
    private Integer categoryId;
    //商品状态
    private Integer status;
    // 页码
    private int page;
    // 每页显示记录数
    private int pageSize;

}
