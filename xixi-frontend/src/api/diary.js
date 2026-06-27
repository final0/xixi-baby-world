import request from './request'

export const createDiary = (data) => request.post('/diary', data)
export const listDiaries = (page = 1, size = 10, month) =>
  request.get('/diary/list', { params: { page, size, month } })
export const getDiary = (id) => request.get(`/diary/${id}`)
export const updateDiary = (id, data) => request.put(`/diary/${id}`, data)
export const deleteDiary = (id) => request.delete(`/diary/${id}`)
