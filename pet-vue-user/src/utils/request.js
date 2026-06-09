import axios from 'axios'
import { ElMessage } from 'element-plus'
import { API_BASE_URL } from '@/config/api'

// 根据环境判断 API 地址
const service = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('authentication')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200 && res.code !== 0) {
      ElMessage.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
    return res
  },
  error => {
    if (error.response) {
      const status = error.response.status

      if (status === 401) {
        console.error('认证失败:', error)
        ElMessage.error('登录已过期，请重新登录')

        localStorage.removeItem('authentication')
        localStorage.removeItem('userInfo')

        setTimeout(() => {
          window.location.href = '/login'
        }, 1500)

        return Promise.reject(error)
      }

      if (status === 403) {
        ElMessage.error('没有权限访问该资源')
        return Promise.reject(error)
      }

      if (status === 404) {
        ElMessage.error('请求的资源不存在')
        return Promise.reject(error)
      }

      if (status === 500) {
        ElMessage.error('服务器内部错误')
        return Promise.reject(error)
      }

      const errorMsg = error.response.data?.msg || error.message || '请求失败'
      ElMessage.error(errorMsg)
      return Promise.reject(error)
    } else if (error.message.includes('timeout')) {
      ElMessage.error('请求超时，请重试')
      return Promise.reject(error)
    } else if (error.message.includes('Network')) {
      ElMessage.error('网络错误，请检查网络连接')
      return Promise.reject(error)
    } else {
      ElMessage.error(error.message || '未知错误')
      return Promise.reject(error)
    }
  }
)

export default service
