import request from '@/utils/request'

export function getOrderList(params) {
  return request({
    url: '/user/order/list',
    method: 'get',
    params
  })
}

export function deleteOrder(ids) {
  return request({
    url: `/user/order/delete/${ids}`,
    method: 'delete'
  })
}