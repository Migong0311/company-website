import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '@/api'

export const useAuthStore = defineStore('auth', () => {
  const isLoggedIn = ref(false)
  const adminName = ref('')

  async function checkLogin() {
    try {
      const { data } = await api.get('/admin/check')
      isLoggedIn.value = data.loggedIn
      adminName.value = data.name || ''
    } catch {
      isLoggedIn.value = false
      adminName.value = ''
    }
  }

  async function login(username, password) {
    const { data } = await api.post('/admin/login', { username, password })
    isLoggedIn.value = true
    adminName.value = data.name
    return data
  }

  async function logout() {
    await api.post('/admin/logout')
    isLoggedIn.value = false
    adminName.value = ''
  }

  return { isLoggedIn, adminName, checkLogin, login, logout }
})
