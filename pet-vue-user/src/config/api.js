const apiBaseUrl = import.meta.env.VITE_API_BASE_URL

if (!apiBaseUrl) {
  throw new Error('缺少 VITE_API_BASE_URL 配置')
}

export const API_BASE_URL = apiBaseUrl.replace(/\/+$/, '')

export function getApiUrl(path = '') {
  if (!path) return API_BASE_URL
  return `${API_BASE_URL}${path.startsWith('/') ? path : `/${path}`}`
}
