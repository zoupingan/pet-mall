import request from '@/utils/request'

export function getShoppingCartList() {
  return request({
    url: '/user/shoppingCart/list',
    method: 'get'
  })
}

export function addToCart(productId, number = 1) {
  return request({
    url: '/user/shoppingCart/add',
    method: 'post',
    data: {
      productId,
      number
    }
  })
}

export function subtractFromCart(productId) {
  return request({
    url: '/user/shoppingCart/subtract',
    method: 'post',
    data: {
      productId: productId
    }
  })
}

export function removeProductFromCart(productId) {
  return request({
    url: '/user/shoppingCart/subtractAll',
    method: 'post',
    data: {
      productId
    }
  })
}

export function cleanShoppingCart() {
  return request({
    url: '/user/shoppingCart/clean',
    method: 'delete'
  })
}
