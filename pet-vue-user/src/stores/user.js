import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref({
    name: '',
    email: '',
    avatar: ''
  })

  const isLoggedIn = computed(() => !!userInfo.value.name)

  function setUserInfo(info) {
    userInfo.value = { ...userInfo.value, ...info }
    localStorage.setItem('isLoggedIn', 'true')
  }

  function clearUserInfo() {
    userInfo.value = {
      name: '',
      email: '',
      avatar: ''
    }
    localStorage.setItem('isLoggedIn', 'false')
  }

  function logout() {
    clearUserInfo()
    window.location.href = '/login'
  }

  return {
    userInfo,
    isLoggedIn,
    setUserInfo,
    clearUserInfo,
    logout
  }
})
