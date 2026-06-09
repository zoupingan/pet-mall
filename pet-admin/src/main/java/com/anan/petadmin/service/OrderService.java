package com.anan.petadmin.service;

import com.anan.petadmin.dto.OrderPageQueryDTO;
import com.anan.petadmin.dto.OrdersPaymentDTO;
import com.anan.petadmin.entity.Order;
import com.anan.petadmin.result.PageResult;

import java.util.List;

public interface OrderService {
    PageResult list(OrderPageQueryDTO orderPageQueryDTO);

    Order update(Order order);

    Integer delete(List<Long> ids);

    Order add(Order order);

    void cancelOrderById(Long id);


    PageResult page(int page, int pageSize, Integer status);

    void payment(OrdersPaymentDTO ordersPaymentDTO);

    PageResult getUserOrders(OrderPageQueryDTO orderPageQueryDTO);
}
