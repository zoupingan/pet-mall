import request from '@/utils/request'
import type { Order, PaginationParams, PaginatedData } from '@/types/api'

export const orderApi = {
  getOrderList(params: PaginationParams & { keyword?: string; status?: number }) {
    return request.get<PaginatedData<Order>>('/order/list', { params })
  },

  getOrderDetail(id: number) {
    return request.get<Order>(`/order/${id}`)
  },

  updateOrderStatus(id: number, status: number) {
    return request.put(`/order/${id}/status`, { status })
  }
}
