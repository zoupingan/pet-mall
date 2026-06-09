import request from '@/utils/request'
import type { ReturnApply, PaginationParams, PaginatedData } from '@/types/api'

export const returnApi = {
  getReturnList(params: PaginationParams & { keyword?: string; status?: number }) {
    return request.get<PaginatedData<ReturnApply>>('/return/list', { params })
  },

  getReturnDetail(id: number) {
    return request.get<ReturnApply>(`/return/${id}`)
  },

  approveReturn(id: number) {
    return request.put(`/return/${id}/approve`)
  },

  rejectReturn(id: number, reason: string) {
    return request.put(`/return/${id}/reject`, { reason })
  }
}
