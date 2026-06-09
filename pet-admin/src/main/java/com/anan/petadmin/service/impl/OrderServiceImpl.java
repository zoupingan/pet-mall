package com.anan.petadmin.service.impl;

import cn.hutool.core.util.IdUtil;
import com.anan.petadmin.context.BaseContext;
import com.anan.petadmin.dto.OrderPageQueryDTO;
import com.anan.petadmin.dto.OrdersPaymentDTO;
import com.anan.petadmin.entity.AddressBook;
import com.anan.petadmin.entity.Order;
import com.anan.petadmin.entity.OrderDetail;
import com.anan.petadmin.entity.Product;
import com.anan.petadmin.entity.ShoppingCart;
import com.anan.petadmin.mapper.AddressBookMapper;
import com.anan.petadmin.mapper.OrderDetailMapper;
import com.anan.petadmin.mapper.OrderMapper;
import com.anan.petadmin.mapper.ProductMapper;
import com.anan.petadmin.mapper.ShoppingCartMapper;
import com.anan.petadmin.result.PageResult;
import com.anan.petadmin.service.OrderService;
import com.anan.petadmin.vo.OrderVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private static final String STOCK_LOCK_KEY = "pet:order:stock-lock";

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private ShoppingCartMapper shoppingCartMapper;
    @Resource
    private OrderDetailMapper orderDetailMapper;
    @Resource
    private AddressBookMapper addressBookMapper;
    @Resource
    private ProductMapper productMapper;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private TransactionTemplate transactionTemplate;

    @Override
    public PageResult list(OrderPageQueryDTO orderPageQueryDTO) {
        int page = orderPageQueryDTO.getPage();
        int pageSize = orderPageQueryDTO.getPageSize();
        log.info("订单分页参数：page={}, pageSize={}", page, pageSize);

        PageHelper.startPage(page, pageSize);

        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .like(orderPageQueryDTO.getOrderNo() != null,
                        Order::getOrderNo, orderPageQueryDTO.getOrderNo())
                .like(orderPageQueryDTO.getStatus() != null,
                        Order::getStatus, orderPageQueryDTO.getStatus())
                .ge(orderPageQueryDTO.getStartTime() != null,
                        Order::getCreateTime, orderPageQueryDTO.getStartTime())
                .le(orderPageQueryDTO.getEndTime() != null,
                        Order::getCreateTime, orderPageQueryDTO.getEndTime());

        List<Order> orderList = orderMapper.selectList(queryWrapper);
        PageInfo<Order> pageInfo = new PageInfo<>(orderList);
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public Order update(Order order) {
        order.setUpdateTime(LocalDateTime.now());
        if (orderMapper.updateById(order) <= 0) {
            throw new RuntimeException("更新订单失败");
        }
        return orderMapper.selectById(order.getId());
    }

    @Override
    public Integer delete(List<Long> ids) {
        int rows = orderMapper.deleteByIds(ids);
        if (rows <= 0) {
            throw new RuntimeException("删除订单失败");
        }
        return rows;
    }

    @Override
    public Order add(Order order) {
        if (orderMapper.insert(order) <= 0) {
            throw new RuntimeException("添加订单失败");
        }
        return orderMapper.selectById(order.getId());
    }

    @Override
    public void cancelOrderById(Long id) {
    }

    @Override
    public PageResult page(int page, int pageSize, Integer status) {
        return null;
    }

    @Override
    public void payment(OrdersPaymentDTO ordersPaymentDTO) {
        if (ordersPaymentDTO == null || ordersPaymentDTO.getAddressBookId() == null) {
            throw new RuntimeException("请选择收货地址");
        }

        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }

        RLock lock = redissonClient.getLock(STOCK_LOCK_KEY);
        boolean locked = false;

        try {
            // 最多等待 3 秒。获取锁后由 Redisson 看门狗自动续期。
            locked = lock.tryLock(3, TimeUnit.SECONDS);
            if (!locked) {
                throw new RuntimeException("当前下单人数较多，请稍后重试");
            }

            transactionTemplate.executeWithoutResult(status ->
                    createOrder(userId, ordersPaymentDTO.getAddressBookId())
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("下单失败，请重新提交");
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private void createOrder(Long userId, Integer addressBookId) {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> cartList = shoppingCartMapper.selectList(queryWrapper);

        if (cartList == null || cartList.isEmpty()) {
            throw new RuntimeException("购物车为空");
        }

        int totalAmount = 0;
        for (ShoppingCart cart : cartList) {
            deductStock(cart);
            totalAmount += cart.getAmount() * cart.getNumber();
        }

        LocalDateTime now = LocalDateTime.now();
        Order order = new Order();
        order.setOrderNo("ORDER" + IdUtil.getSnowflakeNextIdStr());
        order.setUserId(userId);
        order.setStatus(0);
        order.setAddressBookId(addressBookId);
        order.setCreateTime(now);
        order.setUpdateTime(now);
        order.setTotalAmount(totalAmount);

        orderMapper.insert(order);

        for (ShoppingCart cart : cartList) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(order.getId());
            orderDetail.setProductId(cart.getProductId());
            orderDetail.setProductName(cart.getName());
            orderDetail.setQuantity(cart.getNumber());
            orderDetail.setPrice(cart.getAmount());

            orderDetailMapper.insert(orderDetail);
            shoppingCartMapper.deleteById(cart.getId());
        }
    }

    private void deductStock(ShoppingCart cart) {
        LambdaUpdateWrapper<Product> updateWrapper = new LambdaUpdateWrapper<Product>()
                .eq(Product::getId, cart.getProductId())
                .eq(Product::getStatus, 1)
                .ge(Product::getStock, cart.getNumber())
                .setDecrBy(Product::getStock, cart.getNumber());

        int affectedRows = productMapper.update(null, updateWrapper);
        if (affectedRows == 0) {
            throw new RuntimeException("商品【" + cart.getName() + "】库存不足");
        }
    }

    @Override
    @Transactional
    public PageResult getUserOrders(OrderPageQueryDTO orderPageQueryDTO) {
        int page = orderPageQueryDTO.getPage();
        int pageSize = orderPageQueryDTO.getPageSize();
        log.info("订单分页参数：page={}, pageSize={}", page, pageSize);

        PageHelper.startPage(page, pageSize);

        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(orderPageQueryDTO.getStatus() != null,
                        Order::getStatus, orderPageQueryDTO.getStatus())
                .eq(Order::getUserId, BaseContext.getCurrentId());

        List<Order> orderList = orderMapper.selectList(queryWrapper);
        List<OrderVO> orderVOS = new ArrayList<>();
        for (Order order : orderList) {
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(order, orderVO);

            Integer addressBookId = order.getAddressBookId();
            if (addressBookId != null) {
                AddressBook addressBook = addressBookMapper.selectById(addressBookId);
                if (addressBook != null) {
                    orderVO.setOrderAddress(addressBook.getDetail());
                } else {
                    log.warn("收货地址不存在，addressBookId={}", addressBookId);
                    orderVO.setOrderAddress("收货地址已删除");
                }
            }

            LambdaQueryWrapper<OrderDetail> detailQuery = new LambdaQueryWrapper<>();
            detailQuery.eq(OrderDetail::getOrderId, order.getId());
            orderVO.setOrderDetailList(orderDetailMapper.selectList(detailQuery));
            orderVOS.add(orderVO);
        }

        PageInfo<OrderVO> pageInfo = new PageInfo<>(orderVOS);
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }
}
