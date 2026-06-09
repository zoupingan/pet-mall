package com.anan.petadmin.service.impl;

import com.anan.petadmin.context.BaseContext;
import com.anan.petadmin.dto.OrdersPaymentDTO;
import com.anan.petadmin.entity.Order;
import com.anan.petadmin.entity.OrderDetail;
import com.anan.petadmin.entity.Product;
import com.anan.petadmin.entity.ShoppingCart;
import com.anan.petadmin.mapper.OrderDetailMapper;
import com.anan.petadmin.mapper.OrderMapper;
import com.anan.petadmin.mapper.ProductMapper;
import com.anan.petadmin.mapper.ShoppingCartMapper;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTests {

    @Mock
    private OrderMapper orderMapper;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    @Mock
    private OrderDetailMapper orderDetailMapper;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private RedissonClient redissonClient;
    @Mock
    private RLock productLock;
    @Mock
    private TransactionTemplate transactionTemplate;
    @Mock
    private TransactionStatus transactionStatus;

    private OrderServiceImpl orderService;

    @BeforeAll
    static void initializeMybatisPlusMetadata() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        MapperBuilderAssistant assistant = new MapperBuilderAssistant(configuration, "");
        TableInfoHelper.initTableInfo(assistant, Product.class);
    }

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl();
        ReflectionTestUtils.setField(orderService, "orderMapper", orderMapper);
        ReflectionTestUtils.setField(orderService, "shoppingCartMapper", shoppingCartMapper);
        ReflectionTestUtils.setField(orderService, "orderDetailMapper", orderDetailMapper);
        ReflectionTestUtils.setField(orderService, "productMapper", productMapper);
        ReflectionTestUtils.setField(orderService, "redissonClient", redissonClient);
        ReflectionTestUtils.setField(orderService, "transactionTemplate", transactionTemplate);

        BaseContext.setCurrentId(10L);
    }

    @AfterEach
    void tearDown() {
        BaseContext.removeCurrentId();
    }

    @Test
    void deductsStockAndCreatesOrderAfterLockAcquired() throws InterruptedException {
        ShoppingCart cart = cartItem(1L, 2L, 3, 100);
        when(shoppingCartMapper.selectList(any())).thenReturn(List.of(cart));
        when(redissonClient.getLock("pet:order:stock-lock")).thenReturn(productLock);
        when(productLock.tryLock(3, TimeUnit.SECONDS)).thenReturn(true);
        when(productLock.isHeldByCurrentThread()).thenReturn(true);
        when(productMapper.update(isNull(), any())).thenReturn(1);
        when(orderMapper.insert(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(88L);
            return 1;
        });
        when(orderDetailMapper.insert(any(OrderDetail.class))).thenReturn(1);
        executeTransactionCallback();

        orderService.payment(paymentRequest());

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderMapper).insert(orderCaptor.capture());
        assertEquals(300, orderCaptor.getValue().getTotalAmount());
        assertTrue(orderCaptor.getValue().getOrderNo().startsWith("ORDER"));

        ArgumentCaptor<OrderDetail> detailCaptor = ArgumentCaptor.forClass(OrderDetail.class);
        verify(orderDetailMapper).insert(detailCaptor.capture());
        assertEquals(88L, detailCaptor.getValue().getOrderId());
        assertEquals(3, detailCaptor.getValue().getQuantity());

        verify(productMapper).update(isNull(), any());
        verify(productLock).unlock();
    }

    @Test
    void stopsCreatingOrderWhenStockIsInsufficient() throws InterruptedException {
        ShoppingCart cart = cartItem(1L, 2L, 3, 100);

        when(shoppingCartMapper.selectList(any())).thenReturn(List.of(cart));
        when(redissonClient.getLock("pet:order:stock-lock")).thenReturn(productLock);
        when(productLock.tryLock(3, TimeUnit.SECONDS)).thenReturn(true);
        when(productLock.isHeldByCurrentThread()).thenReturn(true);
        when(productMapper.update(isNull(), any())).thenReturn(0);
        executeTransactionCallback();

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> orderService.payment(paymentRequest())
        );

        assertTrue(exception.getMessage().contains("库存不足"));
        verify(orderMapper, never()).insert(any(Order.class));
        verify(orderDetailMapper, never()).insert(any(OrderDetail.class));
        verify(productLock).unlock();
    }

    @Test
    void doesNotEnterTransactionWhenStockLockCannotBeAcquired()
            throws InterruptedException {
        when(redissonClient.getLock("pet:order:stock-lock")).thenReturn(productLock);
        when(productLock.tryLock(3, TimeUnit.SECONDS)).thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> orderService.payment(paymentRequest())
        );

        assertTrue(exception.getMessage().contains("下单人数较多"));
        verify(transactionTemplate, never()).executeWithoutResult(any());
        verify(productMapper, never()).update(any(), any());
        verify(productLock, never()).unlock();
    }

    @SuppressWarnings("unchecked")
    private void executeTransactionCallback() {
        doAnswer(invocation -> {
            Consumer<TransactionStatus> callback = invocation.getArgument(0);
            callback.accept(transactionStatus);
            return null;
        }).when(transactionTemplate).executeWithoutResult(any());
    }

    private ShoppingCart cartItem(
            Long cartId,
            Long productId,
            Integer number,
            Integer amount
    ) {
        ShoppingCart cart = new ShoppingCart();
        cart.setId(cartId);
        cart.setUserId(10L);
        cart.setProductId(productId);
        cart.setName("测试商品");
        cart.setNumber(number);
        cart.setAmount(amount);
        return cart;
    }

    private OrdersPaymentDTO paymentRequest() {
        OrdersPaymentDTO request = new OrdersPaymentDTO();
        request.setAddressBookId(6);
        return request;
    }
}
