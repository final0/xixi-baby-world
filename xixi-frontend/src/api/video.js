import request from './request'

export const uploadVideo = (formData) => request.post('/video/upload', formData)
export const listVideos = (page = 1, size = 12) => request.get('/video/list', { params: { page, size } })
export const getPlayUrl = (id) => request.get(`/video/${id}/play-url`)
export const deleteVideo = (id) => request.delete(`/video/${id}`)
