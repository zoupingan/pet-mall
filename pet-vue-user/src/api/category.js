import request from '@/utils/request'

export function getCategoryList() {
  return request({
    url: '/user/category/list',
    method: 'get'
  })
}
