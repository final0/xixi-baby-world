import request from './request'

export const updateBaby = (data) => request.put('/admin/baby', data)
export const getConfigs = (keys) => request.get('/admin/config', { params: { keys } })
export const updateConfig = (configKey, configValue) => request.put('/admin/config', { configKey, configValue })
export const testEmail = (toEmail) => request.post('/admin/config/test-email', { toEmail })
export const listUsers = (page = 1, size = 20) => request.get('/admin/users', { params: { page, size } })
export const updateUserStatus = (id, status) => request.put(`/admin/users/${id}/status`, { status })
export const listMottos = () => request.get('/admin/mottos')
export const addMotto = (content, author) => request.post('/admin/mottos', { content, author })
export const deleteMotto = (id) => request.delete(`/admin/mottos/${id}`)
export const updateMottoStatus = (id, isActive) => request.put(`/admin/mottos/${id}/status`, { isActive })
