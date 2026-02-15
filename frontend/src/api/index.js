import axios from 'axios'
import Swal from 'sweetalert2'

const api = axios.create({
  baseURL: '/api',
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json'
  }
})

let isSessionExpiredShown = false

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401 && !isSessionExpiredShown) {
      isSessionExpiredShown = true

      const { useAuthStore } = await import('@/stores/auth')
      const authStore = useAuthStore()

      if (authStore.isLoggedIn) {
        authStore.clearSession()
        await Swal.fire({
          icon: 'warning',
          title: '세션 만료',
          text: '로그인 세션이 만료되었습니다. 다시 로그인해주세요.',
          confirmButtonText: '확인',
          confirmButtonColor: '#1a3a5c'
        })
      }

      isSessionExpiredShown = false
    }
    return Promise.reject(error)
  }
)

export default api
