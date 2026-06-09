package com.anan.petadmin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

@Data

@TableName("address_book")
public class AddressBook implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    // 用户ID
    private Long userId;

    // 收货人姓名
    private String consignee;

    //收货人性别
    private String sex;

    // 收货人手机号
    private String phone;

    // 省份编码
    private String provinceCode;

    // 省份名称
    private String provinceName;

    // 城市编码
    private String cityCode;

    // 城市名称
    private String cityName;
    // 区县编码
    private String districtCode;
    // 区县名称
    private String districtName;
    // 详细地址
    private String detail;
    // 是否默认 0 否 1是
    private Integer isDefault;
    //标签
    private String label;
}
