package com.anan.petadmin.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AddressPageQueryDTO implements Serializable {

    // 用户ID
    private String userId;
    //收货人名
    private String consignee;
    // 页码
    private int page;
    // 每页显示记录数
    private int pageSize;

}
