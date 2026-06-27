import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000,
})

request.interceptors.request.use(config => {
  const token = localStorage.getItem('xixi_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

request.interceptors.response.use(
  response => {
    const newToken = response.headers['x-refresh-token']
    if (newToken) localStorage.setItem('xixi_token', newToken)
    const data = response.data
    if (data.code === 200) return data
    ElMessage.error(data.message || '请求失败')
    return Promise.reject(data)
  },
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('xixi_token')
      localStorage.removeItem('xixi_user')
      router.push('/login')
    } else if (error.response?.status === 403) {
      ElMessage.error('无权限访问')
    } else {
      ElMessage.error(error.response?.data?.message || '网络错误，请稍后重试')
    }
    return Promise.reject(error)
  }
)

export default request
