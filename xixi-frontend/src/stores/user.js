import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getProfile } from '@/api/user'
import { logout as logoutApi } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(null)
  const userInfo = ref(null)

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.isAdmin === true)
  const isFirstLogin = computed(() => userInfo.value?.firstLogin === true)

  function initFromStorage() {
    token.value = localStorage.getItem('xixi_token')
    const stored = localStorage.getItem('xixi_user')
    if (stored) {
      try { userInfo.value = JSON.parse(stored) } catch (e) {}
    }
  }

  function setLoginData(data) {
    token.value = data.token
    localStorage.setItem('xixi_token', data.token)
    const info = {
      userId: data.userId,
      nickname: data.nickname,
      role: data.role,
      roleMotto: data.roleMotto,
      avatarUrl: data.avatarUrl,
      firstLogin: data.firstLogin,
      isAdmin: data.isAdmin,
    }
    userInfo.value = info
    localStorage.setItem('xixi_user', JSON.stringify(info))
  }

  async function refreshProfile() {
    const res = await getProfile()
    const info = {
      userId: res.data.id,
      nickname: res.data.nickname,
      role: res.data.role,
      roleMotto: res.data.roleMotto,
      avatarUrl: res.data.avatarUrl,
      firstLogin: false,
      isAdmin: res.data.isAdmin,
    }
    userInfo.value = info
    localStorage.setItem('xixi_user', JSON.stringify(info))
  }

  async function logout() {
    try { await logoutApi() } catch (e) {}
    token.value = null
    userInfo.value = null
    localStorage.removeItem('xixi_token')
    localStorage.removeItem('xixi_user')
  }

  return { token, userInfo, isLoggedIn, isAdmin, isFirstLogin,
           initFromStorage, setLoginData, refreshProfile, logout }
})
