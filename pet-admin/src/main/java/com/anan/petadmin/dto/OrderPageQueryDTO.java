package com.anan.petadmin.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderPageQueryDTO implements Serializable {

    // 订单编号
    private String orderNo;
    // 订单状态
    private String status;
    //用户id
    private Long userId;
    // 起始时间
    private String startTime;
    // 结束时间
    private String endTime;
    // 页码
    private int page;
    // 每页显示记录数
    private int pageSize;

}
