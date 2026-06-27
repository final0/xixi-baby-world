import request from './request'

export const selectRole = (role) => request.post('/user/select-role', { role })
export const getProfile = () => request.get('/user/profile')
export const updateProfile = (data) => request.put('/user/profile', data)
export const uploadAvatar = (formData) => request.post('/user/avatar', formData)
export const changePassword = (data) => request.put('/user/password', data)
