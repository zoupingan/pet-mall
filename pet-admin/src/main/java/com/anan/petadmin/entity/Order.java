package com.anan.petadmin.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data

@TableName("orders")
public class Order implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    // 订单编号
    private String orderNo;

    // 用户ID
    private Long userId;

    // 订单金额
    private Integer totalAmount;

    private Integer status;

    // 收货地址ID
    private Integer addressBookId;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime updateTime;
}
