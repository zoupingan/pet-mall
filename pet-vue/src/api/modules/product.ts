import request from '@/utils/request'
import type { Product, PaginationParams, PaginatedData } from '@/types/api'

export const productApi = {
  getProductList(params?: PaginationParams & { keyword?: string; categoryId?: number; status?: number }) {
    return request.get<PaginatedData<Product>>('/admin/product/list', { params })
  },

  getProductDetail(id: number) {
    return request.get<Product>(`/admin/product/${id}`)
  },

  createProduct(data: Partial<Product>) {
    return request.post<Product>('/admin/product', data)
  },

  updateProduct(id: number, data: Partial<Product>) {
    return request.put<Product>(`/admin/product/${id}`, data)
  },

  deleteProduct(id: number) {
    return request.delete(`/admin/product/${id}`)
  }
}
