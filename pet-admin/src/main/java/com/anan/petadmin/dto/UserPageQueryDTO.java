package com.anan.petadmin.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserPageQueryDTO implements Serializable {

    // 用户名
    private String username;
    // 角色
    private Integer status;
    // 页码
    private int page;
    // 每页显示记录数
    private int pageSize;

}
