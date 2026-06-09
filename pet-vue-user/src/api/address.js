import request from '@/utils/request'

export function getAddressList() {
  return request({
    url: '/user/addressBook/list',
    method: 'get'
  })
}

export function submitOrder(addressBookId) {
  return request({
    url: '/user/order/payment',
    method: 'post',
    data: {
      addressBookId: addressBookId
    }
  })
}

export function saveAddress(data) {
  return request({
    url: '/user/addressBook',
    method: 'post',
    data
  })
}
