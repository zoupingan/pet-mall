package com.anan.petadmin.service.impl;

import com.anan.petadmin.context.BaseContext;
import com.anan.petadmin.dto.ShoppingCartDTO;
import com.anan.petadmin.entity.Product;
import com.anan.petadmin.entity.ShoppingCart;
import com.anan.petadmin.mapper.ProductMapper;
import com.anan.petadmin.mapper.ShoppingCartMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceImplTests {

    @Mock
    private ShoppingCartMapper shoppingCartMapper;

    @Mock
    private ProductMapper productMapper;

    private ShoppingCartServiceImpl shoppingCartService;

    @BeforeEach
    void setUp() {
        shoppingCartService = new ShoppingCartServiceImpl();
        ReflectionTestUtils.setField(shoppingCartService, "shoppingCartMapper", shoppingCartMapper);
        ReflectionTestUtils.setField(shoppingCartService, "productMapper", productMapper);
        BaseContext.setCurrentId(10L);
    }

    @AfterEach
    void tearDown() {
        BaseContext.removeCurrentId();
    }

    @Test
    void addsSelectedNumberForNewCartItem() {
        ShoppingCartDTO request = cartRequest(2L, 3);
        Product product = new Product();
        product.setId(2L);
        product.setProductName("测试商品");
        product.setImageUrl("product.jpg");
        product.setPrice(259);

        when(shoppingCartMapper.selectOne(any())).thenReturn(null);
        when(productMapper.selectById(2L)).thenReturn(product);

        shoppingCartService.addShoppingCart(request);

        ArgumentCaptor<ShoppingCart> captor = ArgumentCaptor.forClass(ShoppingCart.class);
        verify(shoppingCartMapper).insert(captor.capture());
        ShoppingCart inserted = captor.getValue();
        assertEquals(10L, inserted.getUserId());
        assertEquals(2L, inserted.getProductId());
        assertEquals(3, inserted.getNumber());
        assertEquals(259, inserted.getAmount());
    }

    @Test
    void accumulatesSelectedNumberForExistingCartItem() {
        ShoppingCartDTO request = cartRequest(2L, 4);
        ShoppingCart existing = new ShoppingCart();
        existing.setId(20L);
        existing.setUserId(10L);
        existing.setProductId(2L);
        existing.setNumber(2);

        when(shoppingCartMapper.selectOne(any())).thenReturn(existing);

        shoppingCartService.addShoppingCart(request);

        ArgumentCaptor<ShoppingCart> captor = ArgumentCaptor.forClass(ShoppingCart.class);
        verify(shoppingCartMapper).updateById(captor.capture());
        assertEquals(6, captor.getValue().getNumber());
        verify(productMapper, never()).selectById(any());
    }

    private ShoppingCartDTO cartRequest(Long productId, Integer number) {
        ShoppingCartDTO request = new ShoppingCartDTO();
        request.setProductId(productId);
        request.setNumber(number);
        return request;
    }
}
