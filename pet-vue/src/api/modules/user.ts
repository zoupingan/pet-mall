import request from '@/utils/request'
import type { LoginParams, RegisterParams, LoginResult, RegisterResult } from '@/types/api'

export const userApi = {
  login(data: LoginParams) {
    return request.post<LoginResult>('/admin/login', data)
  },

  register(data: RegisterParams) {
    return request.post<RegisterResult>('/admin/register', data)
  },

  logout() {
    return request.post('/auth/logout')
  },

  getUserInfo() {
    return request.get('/user/info')
  },

  updateUserInfo(data: any) {
    return request.put('/user/info', data)
  }
}
