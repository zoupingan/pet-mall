package com.anan.petadmin.vo;

import com.anan.petadmin.entity.OrderDetail;
import com.anan.petadmin.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderVO extends Order implements Serializable {

    //订单商品信息
    private String orderProducts;
    //订单地址信息
    private String orderAddress;
    //订单详情
    private List<OrderDetail> orderDetailList;

}
