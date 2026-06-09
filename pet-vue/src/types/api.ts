export interface PaginationParams {
  page: number
  pageSize: number
}

export interface PaginatedData<T> {
  records: T[]
  total: number
}

export interface User {
  id: number
  username: string
  email: string
  avatar?: string
  role: string
  status: number
  createTime: string
}

export interface LoginParams {
  username: string
  password: string
}

export interface RegisterParams {
  username: string
  password: string
  confirmPassword?: string
}

export interface LoginResult {
  token: string
  userInfo: User
}

export interface RegisterResult {
  success: boolean
  message: string
}

export interface Product {
  id: number
  name: string
  productName?: string
  categoryId: number
  brandId: number
  price: number
  costPrice?: number
  stock: number
  image?: string
  imageUrl?: string
  description?: string
  status: number
  sales: number
  isDeleted?: number
  createTime: string
  updateTime?: string
}

export interface Category {
  id: number
  name: string
  parentId?: number
  sort: number
  icon?: string
  children?: Category[]
}

export interface Brand {
  id: number
  name: string
  logo?: string
  description?: string
  sort: number
  status: number
}

export interface Order {
  id: number
  orderNo: string
  userId: number
  totalAmount: number
  payAmount: number
  status: number
  payTime?: string
  deliveryTime?: string
  receiveTime?: string
  createTime: string
}

export interface Review {
  id: number
  orderId: number
  productId: number
  userId: number
  rating: number
  content: string
  images?: string[]
  reply?: string
  createTime: string
}

export interface ReturnApply {
  id: number
  orderNo: string
  productId: number
  reason: string
  amount: number
  status: number
  remark?: string
  createTime: string
}
