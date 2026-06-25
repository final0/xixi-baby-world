import request from './request'

export const authApi = {
  getCaptcha: () => request.get('/auth/captcha'),
  sendEmailCode: (email) => request.post('/auth/send-email-code', { email }),
  register: (data) => request.post('/auth/register', data),
  login: (data) => request.post('/auth/login', data),
  logout: () => request.post('/auth/logout')
}

export const userApi = {
  selectRole: (role) => request.post('/user/select-role', { role }),
  getProfile: () => request.get('/user/profile'),
  updateProfile: (data) => request.put('/user/profile', data),
  uploadAvatar: (fd) => request.post('/user/avatar', fd, { headers: { 'Content-Type': 'multipart/form-data' } }),
  changePassword: (data) => request.put('/user/password', data)
}

export const homeApi = {
  getHomeInfo: () => request.get('/home/info')
}

export const photoApi = {
  upload: (fd) => request.post('/photo/upload', fd, { headers: { 'Content-Type': 'multipart/form-data' } }),
  list: (page = 1, size = 20) => request.get('/photo/list', { params: { page, size } }),
  delete: (id) => request.delete(`/photo/${id}`),
  setFeatured: (id, isFeatured, sortOrder = 0) => request.put(`/photo/${id}/featured`, { isFeatured, sortOrder })
}

export const videoApi = {
  upload: (fd) => request.post('/video/upload', fd, { headers: { 'Content-Type': 'multipart/form-data' }, timeout: 300000 }),
  list: (page = 1, size = 12) => request.get('/video/list', { params: { page, size } }),
  getPlayUrl: (id) => request.get(`/video/${id}/play-url`),
  delete: (id) => request.delete(`/video/${id}`)
}

export const diaryApi = {
  create: (data) => request.post('/diary', data),
  list: (page = 1, size = 10, month = '') => request.get('/diary/list', { params: { page, size, month: month || undefined } }),
  getById: (id) => request.get(`/diary/${id}`),
  update: (id, data) => request.put(`/diary/${id}`, data),
  delete: (id) => request.delete(`/diary/${id}`)
}

export const aiApi = {
  chat: (sessionId, message) => request.post('/ai/chat', { sessionId, message }),
  clearSession: (sessionId) => request.delete(`/ai/session/${sessionId}`)
}

export const adminApi = {
  updateBaby: (data) => request.put('/admin/baby', data),
  getConfig: (keys) => request.get('/admin/config', { params: { keys } }),
  updateConfig: (configKey, configValue) => request.put('/admin/config', { configKey, configValue }),
  testEmail: (toEmail) => request.post('/admin/config/test-email', { toEmail }),
  listUsers: (page = 1, size = 20) => request.get('/admin/users', { params: { page, size } }),
  updateUserStatus: (id, status) => request.put(`/admin/users/${id}/status`, { status }),
  listMottos: () => request.get('/admin/mottos'),
  addMotto: (content, author) => request.post('/admin/mottos', { content, author }),
  deleteMotto: (id) => request.delete(`/admin/mottos/${id}`),
  updateMottoStatus: (id, isActive) => request.put(`/admin/mottos/${id}/status`, { isActive })
}
