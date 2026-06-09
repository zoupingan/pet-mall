package com.anan.petadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 订单明细
 */
@Data
@TableName("order_item")
public class OrderDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long productId;
    //名称
    private String productName;

    //订单id
    private Long orderId;

    //数量
    private Integer quantity;

    //金额
    private Integer price;
}
