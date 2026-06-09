import request from '@/utils/request'

export function getProductListByCategory(params) {
  return request({
    url: '/user/product/list',
    method: 'get',
    params
  })
}

export function getAdminProductList(params) {
  return request({
    url: '/admin/product/list',
    method: 'get',
    params
  })
}

export function getProductDetail(id) {
  return request({
    url: `/user/product/detail/${id}`,
    method: 'get'
  })
}

export function getAllProductList() {
  return request({
    url: '/user/product/all',
    method: 'get'
  })
}
