package com.anan.petadmin.controller.user;

import com.anan.petadmin.dto.OrderPageQueryDTO;
import com.anan.petadmin.dto.OrdersPaymentDTO;
import com.anan.petadmin.result.PageResult;
import com.anan.petadmin.result.Result;
import com.anan.petadmin.service.OrderService;


import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController()
@RequestMapping("/user/order")
@Slf4j
public class OrderUserController {
    @Resource
    private OrderService orderService;

    /**
     * 订单支付
     *
     *
     */
    @PostMapping("/payment")
    public Result payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO)   {
        orderService.payment(ordersPaymentDTO);
        return Result.success();
    }

    //查询历史订单
    @GetMapping("/list")
    public Result<PageResult> list(OrderPageQueryDTO orderPageQueryDTO) {
        PageResult result = orderService.getUserOrders(orderPageQueryDTO);
        return Result.success(result);
    }
    //取消订单
    @DeleteMapping("/delete/{ids}")
    public Result cancel(@PathVariable List<Long> ids) {
        orderService.delete(ids);
        return Result.success();
    }
}
