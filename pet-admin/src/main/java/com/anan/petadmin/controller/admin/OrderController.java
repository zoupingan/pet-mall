package com.anan.petadmin.controller.admin;

import com.anan.petadmin.dto.OrderPageQueryDTO;
import com.anan.petadmin.entity.Order;
import com.anan.petadmin.result.PageResult;
import com.anan.petadmin.result.Result;
import com.anan.petadmin.service.OrderService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/order")
public class OrderController {
    @Resource
    private OrderService orderService;
    @GetMapping("/list")
    public Result<PageResult> list(OrderPageQueryDTO orderPageQueryDTO) {
        PageResult result = orderService.list(orderPageQueryDTO);
        return Result.success(result);
    }

    @PutMapping("/update")
    public Result<Order> update(@RequestBody Order order) {
        Order result = orderService.update(order);
        return Result.success(result);
    }

}
