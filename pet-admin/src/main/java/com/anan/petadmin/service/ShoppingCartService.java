package com.anan.petadmin.service;

import com.anan.petadmin.dto.ShoppingCartDTO;
import com.anan.petadmin.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    List<ShoppingCart> showShoppingCart();

    void clean();

    void subtractShoppingCart(ShoppingCartDTO shoppingCartDTO);

    void clearProdcutById(ShoppingCartDTO shoppingCartDTO);
}
