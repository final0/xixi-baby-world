import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const request = axios.create({ baseURL: '/api', timeout: 30000 })

request.interceptors.request.use(config => {
  const token = localStorage.getItem('xixi_token')
  if (token) config.headers['Authorization'] = `Bearer ${token}`
  return config
})

request.interceptors.response.use(
  response => {
    const newToken = response.headers['x-refresh-token']
    if (newToken) localStorage.setItem('xixi_token', newToken)
    const data = response.data
    if (data.code === 200) return data
    ElMessage.error(data.message || '操作失败')
    return Promise.reject(new Error(data.message))
  },
  error => {
    const status = error.response?.status
    const message = error.response?.data?.message
    if (status === 401) {
      localStorage.removeItem('xixi_token')
      localStorage.removeItem('xixi_user')
      ElMessage.warning('登录已过期，请重新登录')
      router.push('/login')
    } else if (status === 403) {
      ElMessage.error('无权限访问')
    } else {
      ElMessage.error(message || '网络错误，请稍后重试')
    }
    return Promise.reject(error)
  }
)

export default request
