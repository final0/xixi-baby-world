import request from './request'

export const uploadPhoto = (formData) => request.post('/photo/upload', formData)
export const listPhotos = (page = 1, size = 20) => request.get('/photo/list', { params: { page, size } })
export const deletePhoto = (id) => request.delete(`/photo/${id}`)
export const setFeatured = (id, isFeatured, sortOrder = 0) =>
  request.put(`/photo/${id}/featured`, { isFeatured, sortOrder })
