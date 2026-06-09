const TOKEN_KEY = 'token'
const USERNAME_KEY = 'username'

export const tokenManager = {
  setToken(token: string) {
    localStorage.setItem(TOKEN_KEY, token)
    console.log('✅ Token 已保存')
  },

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY)
  },

  removeToken() {
    localStorage.removeItem(TOKEN_KEY)
    console.log('🗑️ Token 已清除')
  },

  hasToken(): boolean {
    return !!localStorage.getItem(TOKEN_KEY)
  },

  setUsername(username: string) {
    localStorage.setItem(USERNAME_KEY, username)
  },

  getUsername(): string | null {
    return localStorage.getItem(USERNAME_KEY)
  },

  removeUsername() {
    localStorage.removeItem(USERNAME_KEY)
  },

  clearAll() {
    this.removeToken()
    this.removeUsername()
    console.log('🧹 所有登录信息已清除')
  }
}

if (typeof window !== 'undefined') {
  (window as any).tokenManager = tokenManager
}
