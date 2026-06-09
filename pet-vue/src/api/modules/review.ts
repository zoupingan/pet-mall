import request from '@/utils/request'
import type { Review, PaginationParams, PaginatedData } from '@/types/api'

export const reviewApi = {
  getReviewList(params: PaginationParams & { keyword?: string; rating?: number }) {
    return request.get<PaginatedData<Review>>('/review/list', { params })
  },

  getReviewDetail(id: number) {
    return request.get<Review>(`/review/${id}`)
  },

  replyReview(id: number, reply: string) {
    return request.put(`/review/${id}/reply`, { reply })
  },

  deleteReview(id: number) {
    return request.delete(`/review/${id}`)
  }
}
