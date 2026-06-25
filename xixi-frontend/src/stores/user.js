import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { userApi } from '@/api'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref(JSON.parse(localStorage.getItem('xixi_user') || 'null'))
  const token = ref(localStorage.getItem('xixi_token') || '')
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.isAdmin === true)
  const isFirstLogin = computed(() => userInfo.value?.firstLogin === true)

  function setLoginData(data) {
    token.value = data.token
    userInfo.value = { userId: data.userId, nickname: data.nickname, role: data.role,
      roleMotto: data.roleMotto, avatarUrl: data.avatarUrl, firstLogin: data.firstLogin, isAdmin: data.isAdmin }
    localStorage.setItem('xixi_token', data.token)
    localStorage.setItem('xixi_user', JSON.stringify(userInfo.value))
  }

  function updateUserInfo(data) {
    userInfo.value = { ...userInfo.value, ...data }
    localStorage.setItem('xixi_user', JSON.stringify(userInfo.value))
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('xixi_token')
    localStorage.removeItem('xixi_user')
  }

  async function fetchProfile() {
    try { const res = await userApi.getProfile(); updateUserInfo(res.data) } catch {}
  }

  return { userInfo, token, isLoggedIn, isAdmin, isFirstLogin, setLoginData, updateUserInfo, logout, fetchProfile }
})
