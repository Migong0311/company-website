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

  async function changePassword(currentPassword, newPassword) {
    const { data } = await api.put('/admin/password', { currentPassword, newPassword })
    return data
  }

  async function getAdminList() {
    const { data } = await api.get('/admin/list')
    return data
  }

  async function registerAdmin(username, password, name) {
    const { data } = await api.post('/admin/register', { username, password, name })
    return data
  }

  async function deleteAdmin(id) {
    const { data } = await api.delete(`/admin/${id}`)
    return data
  }

  function clearSession() {
    isLoggedIn.value = false
    adminName.value = ''
  }

  return {
    isLoggedIn, adminName,
    checkLogin, login, logout, clearSession,
    changePassword, getAdminList, registerAdmin, deleteAdmin
  }
})
